package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.logging.NotableEvents;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ApplicationContextLocator;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.MainPageSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductBlockSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductPageSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import io.micrometer.core.annotation.Timed;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParserService implements WinestyleParserService {
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    private final RepositoryService repositoryService;

    @Setter
    private AlcoholType alcoholType;
    @Setter
    private String mainPageUrl;

    private static final String PARSING_PROCESS_DURATION_SUMMARY = "parsing_process_duration";

    @Value("${spring.jsoup.scraping.interval.millis}")
    private int timeout;
    @Value("${spring.jsoup.pagination.css.query.main-bottom}")
    private String paginationElementCssQuery;

    @SuppressWarnings("unused")
    @InjectEventLogger
    private EventLogger eventLogger;

    private final Scraper scraper;

    private int parsed = 0;

    @Timed(PARSING_PROCESS_DURATION_SUMMARY)
    @Override
    public void parseBuildSave(String alcoholUrlPart) throws InterruptedException {

        LocalDateTime start = LocalDateTime.now();
        String alcoholUrl = mainPageUrl + alcoholUrlPart;

        LocalDateTime mainFetchingStart = LocalDateTime.now();
        Document currentDoc = scraper.getJsoupDocument(alcoholUrl);
        WinestyleParserServiceMetricsCollector.sumPageFetchingDuration(mainFetchingStart, LocalDateTime.now());

        int pagesNumber = getPagesNumber(currentDoc);
        int nextPageNumber = 2;

        log.warn("Starting parsing of {}", alcoholType);
        int unparsed = 0;

        try {
            while (true) {
                log.info("Parsing: {}", currentDoc.location());

                try {
                    Integer currentUnparsed = new MainJob(currentDoc, start, scraper).call();
                    unparsed += currentUnparsed;
                }
                catch (Exception e) {
                    eventLogger.warn(NotableEvents.W_WINE_PAGE_PARSING_FAILED, alcoholUrl + "?page=" + (nextPageNumber - 1));
                    unparsed += 20;
                }

                if (nextPageNumber > pagesNumber) {
                    break;
                }

                mainFetchingStart = LocalDateTime.now();
                currentDoc = scraper.getJsoupDocument(alcoholUrl + "?page=" + nextPageNumber);
                WinestyleParserServiceMetricsCollector.sumPageFetchingDuration(mainFetchingStart, LocalDateTime.now());

                nextPageNumber++;
            }
        } finally {
            log.info("Finished parsing of {} in {}", alcoholType, java.time.Duration.between((start), LocalDateTime.now()));

            log.debug("Unparsed {}: {}", alcoholType, unparsed);

            parsed = 0;
        }
    }

    private int getPagesNumber(Document doc) {
        try {
            return Integer.parseInt(doc.selectFirst(paginationElementCssQuery).text());
        } catch (NullPointerException ex) {
            log.info("{} does not contain a pagination element: the number of pages is set to 1", doc.location());
            return 1;
        }
    }

    @RequiredArgsConstructor
    private class MainJob implements Callable<Integer> {
        private final Document currentDoc;
        private final LocalDateTime start;
        private final Scraper scraper;
        private final MainPageSegmentor mainPageSegmentor = ApplicationContextLocator.getApplicationContext().getBean(MainPageSegmentor.class);

        /**
         * Парсер страницы с позициями
         */
        @Override
        public Integer call() throws InterruptedException {
            LocalDateTime mainParsingStart = LocalDateTime.now();
            Elements productElements = mainPageSegmentor.extractProductElements(currentDoc);

            int parsedNow = 0;
            int unparsed = 0;

            List<Pair<ProductJob, String>> parsingFutures = new ArrayList<>();
            ProductJob productJob;
            String productUrl;

            for (Element productElement : productElements) {
                productJob = new ProductJob(productElement, scraper);
                try {
                    productUrl = productJob.new ProductUrlJob().call();
                } catch (Exception e) {
                    log.error("Critical error during execution of url from product block {}", productElement.html());
                    continue;
                }
                parsingFutures.add(new Pair<>(productJob, productUrl));
            }

            Pair<ProductJob, String> currentPair;
            Alcohol result;

            for (int i = 0; i < productElements.size(); i++) {
                currentPair = parsingFutures.get(i);
                productUrl = currentPair.getUrl();
                try {
                    result = currentPair.getParsingJob().call();
                    eventLogger.info(NotableEvents.I_WINE_DETAILS_PARSED, productUrl, result);
                    parsedNow += 1;
                } catch (Exception e) {
                    eventLogger.warn(NotableEvents.W_WINE_DETAILS_PARSING_FAILED, productUrl);
                    unparsed += 1;
                }
                Thread.sleep(timeout);
            }

            WinestyleParserServiceMetricsCollector.sumPageParsingDuration(mainParsingStart, LocalDateTime.now());

            eventLogger.info(NotableEvents.I_WINE_PAGE_PARSED, currentDoc.location());

            countParsed(parsedNow);

            logParsed(alcoholType, start);

            return unparsed;
        }

        private synchronized void countParsed(int parsedNow) {
            parsed += parsedNow;
        }

        private void logParsed(AlcoholType alcoholType, LocalDateTime start) {
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

        @RequiredArgsConstructor
        private class Pair<L, R> {
            @Getter
            private final L parsingJob;
            @Getter
            private final R url;
        }
    }

    @RequiredArgsConstructor
    private class ProductJob implements Callable<Alcohol> {
        private final Element productElement;
        private final Scraper scraper;
        private final Director director = new ParserDirector();
        private final ProductBlockSegmentor productBlockSegmentor = ApplicationContextLocator.getApplicationContext().getBean(ProductBlockSegmentor.class);
        private final Parser parser = ApplicationContextLocator.getApplicationContext().getBean(Parser.class);
        private String productUrl;

        @Override
        public Alcohol call() {
            log.info("Now parsing url: {}", productUrl);

            LocalDateTime productParsingStart = LocalDateTime.now();

            Alcohol alcohol;
            ParserApi.WineParsedEvent.Builder kafkaMessageBuilder = ParserApi.WineParsedEvent.newBuilder().setShopLink(mainPageUrl);

            try {
                alcohol = repositoryService.getByUrl(productUrl);

                if (LocalDateTime.now().getDayOfWeek() == DayOfWeek.MONDAY) {
                    alcohol = parseProduct(kafkaMessageBuilder);
                } else {
                    alcohol.setPrice(parser.parsePrice().orElse(null));
                    alcohol.setRating(parser.parseWinestyleRating().orElse(null));
                    repositoryService.add(alcohol);
                    kafkaMessageSender.sendMessage(kafkaMessageBuilder.addWines(director.fillKafkaMessageBuilder(alcohol, alcoholType)).build());
                }
            } catch (NoEntityException ex) {
                alcohol = parseProduct(kafkaMessageBuilder);
            }

            WinestyleParserServiceMetricsCollector.sumDetailsParsingDuration(productParsingStart, LocalDateTime.now());
            WinestyleParserServiceMetricsCollector.incPublished();

            return alcohol;
        }

        private Alcohol parseProduct(ParserApi.WineParsedEvent.Builder kafkaMessageBuilder) {
            Document product = null;

            LocalDateTime detailsFetchingStart = LocalDateTime.now();
            try {
                product = scraper.getJsoupDocument(mainPageUrl + productUrl);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            WinestyleParserServiceMetricsCollector.sumDetailsFetchingDuration(detailsFetchingStart, LocalDateTime.now());

            prepareParsingService(product);

            Alcohol alcohol = director.makeAlcohol(parser, mainPageUrl, productUrl, alcoholType);

            repositoryService.add(alcohol);

            kafkaMessageSender.sendMessage(kafkaMessageBuilder.addWines(director.getKafkaMessageBuilder()).build());

            return alcohol;
        }

        private void prepareParsingService(Document doc) {
            ProductPageSegmentor productPageSegmentor = ApplicationContextLocator.getApplicationContext().getBean(ProductPageSegmentor.class);

            Element productPageMainContent = productPageSegmentor.extractProductPageMainContent(doc);

            parser.setListDescription(productBlockSegmentor.extractListDescription(productElement));
            parser.setLeftBlock(productPageSegmentor.extractLeftBlock(productPageMainContent));
            parser.setArticlesBlock(productPageSegmentor.extractArticlesBlock(productPageMainContent));
            parser.setDescriptionBlock(productPageSegmentor.extractDescriptionBlock(productPageMainContent));
        }

        @RequiredArgsConstructor
        private class ProductUrlJob implements Callable<String> {
            @Override
            public String call() {
                parser.setProductBlock(productElement);
                parser.setInfoContainer(productBlockSegmentor.extractInfoContainer(productElement));

                productUrl = parser.parseUrl();
                return productUrl;
            }
        }
    }
}
