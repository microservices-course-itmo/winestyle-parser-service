package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.ParserDirectorService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.ScrapingService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.ScrapingServicePooledObjectFactory;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.SegmentationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParserService implements WinestyleParserService {
    private final ParsingService parsingService;
    private final SegmentationService segmentationService;
    private final RepositoryService alcoholRepositoryService;
    private final ParserDirectorService parserDirectorService;
    private final KafkaMessageSender<UpdateProducts.UpdateProductsMessage> kafkaSendMessageService;
    private final Alcohol.AlcoholBuilder builder = Alcohol.builder();

    private final int MAX_THREAD_COUNT = 50;

    private final GenericObjectPoolConfig<ScrapingService> scrapingServiceGenericObjectPoolConfig =
            new GenericObjectPoolConfig<>();
    private GenericObjectPool<ScrapingService> scrapingServiceObjectPool;

    private final ThreadFactory parsingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Parsing-%d")
            .build();
    private ExecutorService parsingThreadPool;

    private int parsed = 0;

    @SneakyThrows
    @PostConstruct
    public void poolsInit() {
        scrapingServiceGenericObjectPoolConfig.setMaxTotal(MAX_THREAD_COUNT);
        scrapingServiceGenericObjectPoolConfig.setMaxIdle(MAX_THREAD_COUNT);

        scrapingServiceObjectPool = new GenericObjectPool<>(
                new ScrapingServicePooledObjectFactory(),
                scrapingServiceGenericObjectPoolConfig
        );

        scrapingServiceObjectPool.addObjects(MAX_THREAD_COUNT);

        parsingThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT, parsingThreadFactory);
    }

    @SneakyThrows
    public void poolsRenew() {
        if (scrapingServiceObjectPool.isClosed()) {
            scrapingServiceObjectPool = new GenericObjectPool<>(
                    new ScrapingServicePooledObjectFactory(),
                    scrapingServiceGenericObjectPoolConfig
            );
            scrapingServiceObjectPool.addObjects(MAX_THREAD_COUNT);
        }

        if (parsingThreadPool.isShutdown()) {
            parsingThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT, parsingThreadFactory);
        }
    }

    @SneakyThrows
    @Override
    public void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) {
        poolsRenew();

        LocalDateTime start = LocalDateTime.now();
        String alcoholUrl = mainUrl + relativeUrl;

        ScrapingService scrapingService = scrapingServiceObjectPool.borrowObject();
        Document currentDoc = scrapingService.getJsoupDocument(alcoholUrl);
        scrapingServiceObjectPool.returnObject(scrapingService);

        int pagesNumber = getPagesNumber(currentDoc);
        int nextPageNumber = 2;

        log.warn("Starting parsing of {}", alcoholType);

        try {
            while (true) {
                log.info("Parsing: {}", currentDoc.location());

                parsingThreadPool.execute(new ProductJob(mainUrl, currentDoc, alcoholType, start));

                if (nextPageNumber > pagesNumber) {
                    break;
                }

                currentDoc = getDocument(nextPageNumber, alcoholUrl);

                nextPageNumber++;
            }
        } finally {
            parsingThreadPool.shutdown();

            parsingThreadPool.awaitTermination(10, TimeUnit.SECONDS);

            scrapingServiceObjectPool.close();

            log.info("Finished parsing of {} in {}", alcoholType, java.time.Duration.between((start), LocalDateTime.now()));

            final List<Runnable> unparsed = parsingThreadPool.shutdownNow();

            log.debug("Unparsed {}: {}", alcoholType, unparsed.size());

            parsed = 0;
        }
    }

    @SneakyThrows
    public Document getDocument(int pageNumber, String alcoholUrl) {
        ScrapingService scrapingService = scrapingServiceObjectPool.borrowObject();
        Document document = scrapingService.getJsoupDocument(alcoholUrl + "?page=" + pageNumber);
        scrapingServiceObjectPool.returnObject(scrapingService);
        return document;
    }

    private class ProductJob implements Runnable {
        String mainUrl;
        Document currentDoc;
        String alcoholType;
        LocalDateTime start;

        /**
         * @param mainUrl     адрес главной страницы сайта
         * @param currentDoc  текущая страница с позициями
         * @param alcoholType тип алкоголя
         */
        public ProductJob(String mainUrl, Document currentDoc, String alcoholType, LocalDateTime start) {
            this.mainUrl = mainUrl;
            this.currentDoc = currentDoc;
            this.alcoholType = alcoholType;
            this.start = start;
        }

        /**
         * Парсер страницы с позициями
         */
        @SneakyThrows({InterruptedException.class, ExecutionException.class})
        @Override
        public void run() {
            Elements alcohol = segmentationService
                    .setMainDocument(currentDoc)
                    .setMainMainContent()
                    .breakDocumentIntoProductElements();

            int parsedNow = 0;

            for (Element drink : alcohol) {
                parsedNow += parsingThreadPool.submit(() -> {
                    String productUrl;

                    parsingService.setProductBlock(segmentationService.setProductBlock(drink).getProductBlock());
                    parsingService.setInfoContainer(segmentationService.getInfoContainer());

                    productUrl = parsingService.parseUrl();
                    log.info("Now parsing url: {}", productUrl);

                    Alcohol result;
                    try {
                        alcoholRepositoryService.getByUrl(productUrl);
                        alcoholRepositoryService.updatePrice(parsingService.parsePrice(), productUrl);
                        alcoholRepositoryService.updateRating(parsingService.parseWinestyleRating(), productUrl);
                        result = alcoholRepositoryService.getByUrl(productUrl);
                    } catch (NoEntityException ex) {
                        ScrapingService scrapingService = scrapingServiceObjectPool.borrowObject();
                        Document product = scrapingService.getJsoupDocument(mainUrl + productUrl);
                        segmentationService.setProductDocument(product).setProductMainContent();
                        prepareParsingService();
                        parserDirectorService.makeAlcohol(builder, alcoholType);
                        result = builder.url(productUrl).build();
                        alcoholRepositoryService.add(result);
                        scrapingServiceObjectPool.returnObject(scrapingService);
                    }
                    kafkaSendMessageService.sendMessage(
                            UpdateProducts.UpdateProductsMessage.newBuilder()
                                    .setShopLink(mainUrl)
                                    .addProducts(result.asProduct())
                                    .build()
                    );
                    return 1;
                }).get();
            }
            countParsed(parsedNow);
            logParsed(alcoholType, start);
        }

        private synchronized void countParsed(int parsedNow) {
            parsed += parsedNow;
        }

        private void logParsed(String alcoholType, LocalDateTime start) {
            long hoursPassed;
            long minutesPart;
            long secondsPart;
            Duration timePassed = java.time.Duration.between((start), LocalDateTime.now());

            hoursPassed = timePassed.toHours();
            minutesPart = (timePassed.toMinutes() - hoursPassed * 60);
            secondsPart = (timePassed.toSeconds() - hoursPassed * 3600 - minutesPart * 60);

            log.info("Parsing of {}: {} in {} hours {} minutes {} seconds ({} entities per second)",
                    alcoholType, parsed, hoursPassed, minutesPart, secondsPart, parsed / (double) timePassed.toSeconds());
        }

        private void prepareParsingService() {
            parsingService.setListDescription(segmentationService.getListDescription());
            parsingService.setLeftBlock(segmentationService.getLeftBlock());
            parsingService.setArticlesBlock(segmentationService.getArticlesBlock());
            parsingService.setDescriptionBlock(segmentationService.getDescriptionBlock());
        }

    }

    private int getPagesNumber(Document doc) {
        try {
            return Integer.parseInt(doc.selectFirst("#CatalogPagingBottom li:last-of-type").text());
        } catch (NullPointerException ex) {
            log.info("{} does not contain a pagination element: the number of pages is set to 1", doc.location());
            return 1;
        }
    }
}
