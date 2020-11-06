package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.*;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.ParserDirectorService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.ScrapingService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.SegmentationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParserService implements WinestyleParserService {
    private final ParsingService parsingService;
    private final SegmentationService segmentationService;
    private final ScrapingService scrapingService;
    private final RepositoryService alcoholRepositoryService;
    private final ParserDirectorService parserDirectorService;
    private final KafkaMessageSender<UpdateProducts.UpdateProductsMessage> kafkaSendMessageService;
    private final Alcohol.AlcoholBuilder builder = Alcohol.builder();
    private ExecutorService productsParsingExecutor;
    private int parsed = 0;

    @SneakyThrows
    @Override
    public void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) {
        LocalDateTime start = LocalDateTime.now();
        String alcoholUrl = mainUrl + relativeUrl;
        Document currentDoc = scrapingService.getJsoupDocument(alcoholUrl);

        int pagesNumber = getPagesNumber(currentDoc);
        int nextPageNumber = 2;

        log.warn("Starting parsing of {}", alcoholType);

        while (true) {
            log.info("Parsing: {}", currentDoc.location());

            productsParsingExecutor.execute(new ProductJob(mainUrl, currentDoc, alcoholType, start));

            if (nextPageNumber > pagesNumber) {
                break;
            }

            currentPage =  productsParsingExecutor.submit(new ProductRunner(nextPageNumber, alcoholUrl)).get();

            nextPageNumber++;
        }

        log.warn("Finished parsing of {} in {}", alcoholType, java.time.Duration.between((start), LocalDateTime.now()));
    }

    private class ProductRunner implements Callable<Document> {
        int nextPageNumber;
        String alcoholUrl;

        public ProductRunner(int nextPageNumber, String alcoholUrl) {
            this.nextPageNumber = nextPageNumber;
            this.alcoholUrl = alcoholUrl;
        }

        @Override
        @SneakyThrows
        public Document call() {
            return scrapingService.getJsoupDocument(alcoholUrl + "?page=" + nextPageNumber);
        }
    }

    private class ProductJob implements Callable<Integer> {
        String mainUrl;
        Document currentPage;
        String alcoholType;
        LocalDateTime start;
        long hoursPassed;
        long minutesPart;
        long secondsPart;

        public ProductJob(String mainUrl, Document currentPage, String alcoholType, LocalDateTime start) {
            this.mainUrl = mainUrl;
            this.currentPage = currentPage;
            this.alcoholType = alcoholType;
            this.start = start;
        }

        @SneakyThrows
        @Override
        public Integer call() {
            int parsed = parseProducts(mainUrl, currentPage, alcoholType).get();
            Duration timePassed = java.time.Duration.between((start), LocalDateTime.now());

            hoursPassed = timePassed.toHours();
            minutesPart = (timePassed.toMinutes() - hoursPassed * 60);
            secondsPart = (timePassed.toSeconds() - minutesPart * 60);

            productsParsingExecutor.submit(() -> log.info("Parsing of {}: {} in {} hours {} minutes {} seconds ({} entities per second)",
                    alcoholType, parsed, hoursPassed, minutesPart, secondsPart, parsed / (double) timePassed.toSeconds()));

            return parsed;
        }
    }

    /**
     * Парсер страницы с позициями
     *
     * @param mainUrl     адрес главной страницы сайта
     * @param currentDoc  текущая страница с позициями
     * @param alcoholType тип алкоголя
     * @return количество распаршенных позиций
     */
    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    private Future<Integer> parseProducts(String mainUrl, Document currentDoc, String alcoholType) {
        ExecutorService productsExecutor = Executors.newSingleThreadExecutor();

        Future<Integer> parsed = productsExecutor.submit(() -> {
            productsParsingExecutor = Executors.newCachedThreadPool();

            Future<Elements> alcohol = productsParsingExecutor.submit(() -> segmentationService
                    .setMainDocument(currentDoc)
                    .setMainMainContent()
                    .breakDocumentIntoProductElements());

            int parsedNow = 0;

            for (Element drink : alcohol.get()) {
                parsedNow += productsParsingExecutor.submit(() -> {
                    String productUrl;

                    parsingService.setProductBlock(segmentationService.setProductBlock(drink).getProductBlock());
                    parsingService.setInfoContainer(segmentationService.getInfoContainer());

                    productUrl = parsingService.parseUrl();
                    log.debug("Now parsing url: {}", productUrl);

                    Alcohol result = null;
                    try {
                        alcoholRepositoryService.getByUrl(productUrl);
                        try {
                            alcoholRepositoryService.updatePrice(parsingService.parsePrice(), productUrl);
                            alcoholRepositoryService.updateRating(parsingService.parseWinestyleRating(), productUrl);
                            result = alcoholRepositoryService.getByUrl(productUrl);
                        } catch (NoEntityException ignore) {
                        }
                    } catch (NoEntityException ex) {
                        Document product = scrapingService.getJsoupDocument(mainUrl + productUrl);
                        segmentationService.setProductDocument(product).setProductMainContent();
                        prepareParsingService();
                        parserDirectorService.makeAlcohol(builder, alcoholType);
                        result = builder.url(productUrl).build();
                        alcoholRepositoryService.add(result);
                    }
                    kafkaSendMessageService.sendMessage(
                            UpdateProducts.UpdateProductsMessage.newBuilder()
                                    .setShopLink(mainUrl)
                                    .addProducts(Objects.requireNonNull(result).asProduct())
                                    .build()
                    );
                    return 1;
                }).get();
            }
            countParsed(parsedNow);
            logParsed(alcoholType, start);
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

    private void prepareParsingService() {
        parsingService.setListDescription(segmentationService.getListDescription());
        parsingService.setLeftBlock(segmentationService.getLeftBlock());
        parsingService.setArticlesBlock(segmentationService.getArticlesBlock());
        parsingService.setDescriptionBlock(segmentationService.getDescriptionBlock());
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
        secondsPart = (timePassed.toSeconds() - minutesPart * 60);

        log.info("Parsing of {}: {} in {} hours {} minutes {} seconds ({} entities per second)",
                alcoholType, parsed, hoursPassed, minutesPart, secondsPart, parsed / (double) timePassed.toSeconds());
    }
}
