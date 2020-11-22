package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

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

    @PostConstruct
    public void poolsInit() {
        scrapingServiceGenericObjectPoolConfig.setMaxTotal(MAX_THREAD_COUNT);
        scrapingServiceGenericObjectPoolConfig.setMaxIdle(MAX_THREAD_COUNT);

        scrapingServiceObjectPool = new GenericObjectPool<>(
                new ScrapingServicePooledObjectFactory(),
                scrapingServiceGenericObjectPoolConfig
        );

        try {
            scrapingServiceObjectPool.addObjects(MAX_THREAD_COUNT);
        } catch (Exception e) {
            log.error("Error on adding new threads to scrapingServiceObjectPool", e);
        }

        parsingThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT, parsingThreadFactory);
    }

    public void poolsRenew() {
        if (scrapingServiceObjectPool.isClosed()) {
            scrapingServiceObjectPool = new GenericObjectPool<>(
                    new ScrapingServicePooledObjectFactory(),
                    scrapingServiceGenericObjectPoolConfig
            );
            try {
                scrapingServiceObjectPool.addObjects(MAX_THREAD_COUNT);
            } catch (Exception e) {
                log.error("Error on adding new threads to scrapingServiceObjectPool", e);
            }
        }

        if (parsingThreadPool.isShutdown()) {
            parsingThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT, parsingThreadFactory);
        }
    }

    @Override
    public void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) throws InterruptedException {
        poolsRenew();

        LocalDateTime start = LocalDateTime.now();
        String alcoholUrl = mainUrl + relativeUrl;

        ScrapingService scrapingService = null;
        try {
            scrapingService = scrapingServiceObjectPool.borrowObject();
        } catch (Exception e) {
            log.error("Error on borrowing instance of {} from scrapingServiceObjectPool", ScrapingService.class.getSimpleName(), e);
        }
        Document currentDoc = Objects.requireNonNull(scrapingService).getJsoupDocument(alcoholUrl);

        scrapingServiceObjectPool.returnObject(scrapingService);

        int pagesNumber = getPagesNumber(currentDoc);
        int nextPageNumber = 2;

        log.warn("Starting parsing of {}", alcoholType);

        List<Future<Integer>> unparsedFutures = new ArrayList<>();

        try {
            while (true) {
                log.info("Parsing: {}", currentDoc.location());

                unparsedFutures.add(parsingThreadPool.submit(new ProductJob(mainUrl, currentDoc, alcoholType, start)));

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

            int unparsed = 0;

            for (Future<Integer> unparsedFuture : unparsedFutures) {
                try {
                    unparsed += unparsedFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    unparsed += parsingThreadPool.shutdownNow().size();
                }
            }

            log.debug("Unparsed {}: {}", alcoholType, unparsed);

            parsed = 0;
        }
    }

    public Document getDocument(int pageNumber, String alcoholUrl) throws InterruptedException {
        ScrapingService scrapingService = null;

        try {
            scrapingService = scrapingServiceObjectPool.borrowObject();
        } catch (Exception e) {
            log.error("Error on borrowing instance of {} from scrapingServiceObjectPool", ScrapingService.class.getSimpleName(), e);
        }

        Document document = Objects.requireNonNull(scrapingService).getJsoupDocument(alcoholUrl + "?page=" + pageNumber);

        scrapingServiceObjectPool.returnObject(scrapingService);
        return document;
    }

    private class ProductJob implements Callable<Integer> {
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
        @Override
        public Integer call() throws InterruptedException {
            Elements alcohol = segmentationService
                    .setMainDocument(currentDoc)
                    .setMainMainContent()
                    .breakDocumentIntoProductElements();

            int parsedNow = 0;

            List<Future<Integer>> parsingFutures = new ArrayList<>();

            for (Element drink : alcohol) {
                parsingFutures.add(parsingThreadPool.submit(() -> {
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
                }));
            }
            int unparsed = 0;
            for (int i = 0; i < alcohol.size(); i++) {
                try {
                    parsedNow += parsingFutures.get(i).get();
                } catch (ExecutionException e) {
                    unparsed += 1;
                }
            }
            countParsed(parsedNow);
            logParsed(alcoholType, start);
            return unparsed;
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
