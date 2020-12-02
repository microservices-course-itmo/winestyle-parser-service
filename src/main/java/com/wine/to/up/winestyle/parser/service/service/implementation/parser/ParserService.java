package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
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

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParserService implements WinestyleParserService {
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    private final RepositoryService alcoholRepositoryService;

    @Setter
    private AlcoholType alcoholType;
    @Setter
    private String mainPageUrl;

    @Value("${spring.task.execution.pool.size}")
    private int maxThreadCount;
    @Value("${spring.kafka.metrics.service-name}")
    private String parserName;

    private final ThreadFactory mainPageParsingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Main_parser-%d")
            .build();
    private final ThreadFactory productPageParsingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Prod_parser-%d")
            .build();
    private final ThreadFactory urlFetchingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Url_fetching-%d")
            .build();
    private ExecutorService mainPageParsingThreadPool;
    private ExecutorService productPageParsingThreadPool;
    private ExecutorService urlFetchingThreadPool;


    @SuppressWarnings("unused")
    @InjectEventLogger
    private EventLogger eventLogger;

    private int parsed = 0;

    @Value("${spring.jsoup.scraping.proxy-timeout}")
    private int timeout;

    @Value("${spring.jsoup.pagination.css.query.main-bottom}")
    private String paginationElementCssQuery;

    @PostConstruct
    private void initPools() {
        mainPageParsingThreadPool = initPool(maxThreadCount / 20, mainPageParsingThreadFactory);
        productPageParsingThreadPool = initPool(maxThreadCount, productPageParsingThreadFactory);
        urlFetchingThreadPool = initPool(maxThreadCount, urlFetchingThreadFactory);
    }

    private void renewPools() {
        if (mainPageParsingThreadPool.isShutdown()) {
            mainPageParsingThreadPool = initPool(maxThreadCount / 20, mainPageParsingThreadFactory);
        }

        if (productPageParsingThreadPool.isShutdown()) {
            productPageParsingThreadPool = initPool(maxThreadCount, productPageParsingThreadFactory);
        }

        if (urlFetchingThreadPool.isShutdown()) {
            urlFetchingThreadPool = initPool(maxThreadCount, urlFetchingThreadFactory);
        }
    }

    private ExecutorService initPool(int maxThreadCount, ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(maxThreadCount, threadFactory);
    }

    @Timed
    @Override
    public void parseBuildSave(String alcoholUrlPart) throws InterruptedException {
        renewPools();

        LocalDateTime start = LocalDateTime.now();
        String alcoholUrl = mainPageUrl + alcoholUrlPart;

        Scraper mainScraper = new Scraper();
        Scraper productScraper = new Scraper();

        Document currentDoc = mainScraper.getJsoupDocument(alcoholUrl);
        WinestyleParserServiceMetricsCollector.sumPageFetching(parserName);
        int pagesNumber = getPagesNumber(currentDoc);
        int nextPageNumber = 2;

        log.warn("Starting parsing of {}", alcoholType);

        List<Future<Integer>> parseFutures = new ArrayList<>();

        try {
            while (true) {
                log.info("Parsing: {}", currentDoc.location());

                parseFutures.add(mainPageParsingThreadPool.submit(new MainJob(currentDoc, start, productScraper)));

                if (nextPageNumber > pagesNumber) {
                    break;
                }

                currentDoc = mainScraper.getJsoupDocument(alcoholUrl + "?page=" + nextPageNumber);

                WinestyleParserServiceMetricsCollector.sumPageFetching(parserName);

                nextPageNumber++;
            }
        } finally {
            mainPageParsingThreadPool.shutdown();

            mainPageParsingThreadPool.awaitTermination(timeout, TimeUnit.MILLISECONDS);

            log.info("Finished parsing of {} in {}", alcoholType, java.time.Duration.between((start), LocalDateTime.now()));

            int unparsed = 0;

            unparsed += productPageParsingThreadPool.shutdownNow().size();
            mainPageParsingThreadPool.shutdownNow();

            for (int i = 0; i < parseFutures.size() - 1; i++) {
                try {
                    unparsed += parseFutures.get(i).get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    eventLogger.warn(NotableEvents.W_WINE_PAGE_PARSING_FAILED, alcoholUrl + "?page=" + i);
                    unparsed += 20;
                }
            }

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
        @Timed
        @Override
        public Integer call() throws InterruptedException {
            Elements alcohol = mainPageSegmentor.extractProductElements(currentDoc);

            int parsedNow = 0;
            int unparsed = 0;

            List<Pair<Future<Alcohol>, String>> parsingFutures = new ArrayList<>();
            ProductJob productJob;
            String productUrl;
            for (Element drink : alcohol) {
                productJob = new ProductJob(drink, scraper);
                try {
                    productUrl = urlFetchingThreadPool.submit(productJob.new ProductUrlJob()).get();
                } catch (ExecutionException e) {
                    log.error("Critical error during execution of url from product block {}", drink.html());
                    continue;
                }
                parsingFutures.add(new Pair<>(productPageParsingThreadPool.submit(productJob), productUrl));
            }

            Pair<Future<Alcohol>, String> currentPair;
            Alcohol result;
            for (int i = 0; i < alcohol.size(); i++) {
                currentPair = parsingFutures.get(i);
                productUrl = currentPair.getUrl();
                try {
                    result = currentPair.getParsingFuture().get();
                    eventLogger.info(NotableEvents.I_WINE_DETAILS_PARSED, productUrl, result);
                    parsedNow += 1;
                } catch (ExecutionException e) {
                    eventLogger.warn(NotableEvents.W_WINE_DETAILS_PARSING_FAILED, productUrl);
                    unparsed += 1;
                }
            }

            countParsed(parsedNow);
            logParsed(alcoholType, start);
            eventLogger.info(NotableEvents.I_WINE_PAGE_PARSED, currentDoc.location());
            WinestyleParserServiceMetricsCollector.sumPageParsing(parserName);
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
            private final L parsingFuture;
            @Getter
            private final R url;
        }
    }

    @RequiredArgsConstructor
    private class ProductJob implements Callable<Alcohol> {
        private final Element drink;
        private final Scraper scraper;
        private final Director director = new ParserDirector();
        private final ProductBlockSegmentor productBlockSegmentor = ApplicationContextLocator.getApplicationContext().getBean(ProductBlockSegmentor.class);
        private final Parser parser = ApplicationContextLocator.getApplicationContext().getBean(Parser.class);
        private String productUrl;

        @Override
        @Timed
        public Alcohol call() {
            log.info("Now parsing url: {}", productUrl);

            Alcohol alcohol;

            try {
                alcohol = alcoholRepositoryService.getByUrl(productUrl);
                alcoholRepositoryService.updatePrice(parser.parsePrice().orElse(null), productUrl);
                alcoholRepositoryService.updateRating(parser.parseWinestyleRating().orElse(null), productUrl);

                kafkaMessageSender.sendMessage(
                        ParserApi.WineParsedEvent.newBuilder()
                                .setShopLink(mainPageUrl)
                                .addWines(director.fillKafkaMessageBuilder(alcohol, alcoholType))
                                .build()
                );
            } catch (NoEntityException ex) {
                Document product = null;

                try {
                    product = scraper.getJsoupDocument(mainPageUrl + productUrl);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                WinestyleParserServiceMetricsCollector.sumDetailsFetching(parserName);

                prepareParsingService(product);

                alcohol = director.makeAlcohol(parser, mainPageUrl, productUrl, alcoholType);
                alcoholRepositoryService.add(alcohol);

                kafkaMessageSender.sendMessage(
                        ParserApi.WineParsedEvent.newBuilder()
                                .setShopLink(mainPageUrl)
                                .addWines(director.getKafkaMessageBuilder())
                                .build()
                );
            }
            WinestyleParserServiceMetricsCollector.incPublished(parserName);
            WinestyleParserServiceMetricsCollector.sumDetailsParsing(parserName);

            return alcohol;
        }

        private void prepareParsingService(Document doc) {
            ProductPageSegmentor productPageSegmentor = ApplicationContextLocator.getApplicationContext().getBean(ProductPageSegmentor.class);

            Element productPageMainContent = productPageSegmentor.extractProductPageMainContent(doc);

            parser.setListDescription(productBlockSegmentor.extractListDescription(drink));
            parser.setLeftBlock(productPageSegmentor.extractLeftBlock(productPageMainContent));
            parser.setArticlesBlock(productPageSegmentor.extractArticlesBlock(productPageMainContent));
            parser.setDescriptionBlock(productPageSegmentor.extractDescriptionBlock(productPageMainContent));
        }

        @RequiredArgsConstructor
        private class ProductUrlJob implements Callable<String> {
            @Override
            public String call() {
                parser.setProductBlock(drink);
                parser.setInfoContainer(productBlockSegmentor.extractInfoContainer(drink));

                productUrl = parser.parseUrl();
                return productUrl;
            }
        }
    }
}
