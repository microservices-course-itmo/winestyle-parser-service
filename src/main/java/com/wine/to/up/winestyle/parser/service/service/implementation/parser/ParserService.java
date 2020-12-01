package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ApplicationContextLocator;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.Segmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
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

    private final ThreadFactory parsingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Parsing-%d")
            .build();
    private ExecutorService parsingThreadPool;

    private int parsed = 0;

    @PostConstruct
    public void initPools() {
        parsingThreadPool = Executors.newFixedThreadPool(maxThreadCount, parsingThreadFactory);
    }

    public void renewPool() {
        if (parsingThreadPool.isShutdown()) {
            parsingThreadPool = Executors.newFixedThreadPool(maxThreadCount, parsingThreadFactory);
        }
    }

    @Override
    public void parseBuildSave(String alcoholUrlPart) throws InterruptedException {
        renewPool();

        LocalDateTime start = LocalDateTime.now();
        String alcoholUrl = mainPageUrl + alcoholUrlPart;

        Scraper mainScraper = new Scraper();
        Scraper productScraper = new Scraper();

        Document currentDoc = mainScraper.getJsoupDocument(alcoholUrl);
        int pagesNumber = getPagesNumber(currentDoc);
        int nextPageNumber = 2;

        log.warn("Starting parsing of {}", alcoholType);

        List<Future<Integer>> unparsedFutures = new ArrayList<>();

        try {
            while (true) {
                log.info("Parsing: {}", currentDoc.location());

                unparsedFutures.add(parsingThreadPool.submit(new MainJob(currentDoc, start, productScraper)));

                if (nextPageNumber > pagesNumber) {
                    break;
                }

                currentDoc = mainScraper.getJsoupDocument(alcoholUrl + "?page=" + nextPageNumber);

                nextPageNumber++;
            }
        } finally {
            parsingThreadPool.shutdown();

            parsingThreadPool.awaitTermination(10, TimeUnit.SECONDS);

            log.info("Finished parsing of {} in {}", alcoholType, java.time.Duration.between((start), LocalDateTime.now()));

            int unparsed = 0;

            for (Future<Integer> unparsedFuture : unparsedFutures) {
                try {
                    unparsed += unparsedFuture.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    unparsed += parsingThreadPool.shutdownNow().size();
                }
            }

            log.debug("Unparsed {}: {}", alcoholType, unparsed);

            parsed = 0;
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

    @RequiredArgsConstructor
    private class MainJob implements Callable<Integer> {
        private final Document currentDoc;
        private final LocalDateTime start;
        private final Scraper scraper;
        private final Segmentor segmentor = ApplicationContextLocator.getApplicationContext().getBean(Segmentor.class);

        /**
         * Парсер страницы с позициями
         */
        @Override
        public Integer call() throws InterruptedException {
            Elements alcohol = segmentor
                    .setMainDocument(currentDoc)
                    .breakDocumentIntoProductElements();

            int parsedNow = 0;
            int unparsed = 0;

            List<Future<?>> parsingFutures = new ArrayList<>();

            for (Element drink : alcohol) {
                parsingFutures.add(parsingThreadPool.submit(new ProductJob(drink, scraper)));
            }

            for (int i = 0; i < alcohol.size(); i++) {
                try {
                    parsingFutures.get(i).get();
                    parsedNow += 1;
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
    }

    @RequiredArgsConstructor
    private class ProductJob implements Runnable {
        private final Element alcohol;
        private final Scraper scraper;
        private final Director director = new ParserDirector();
        private final Segmentor segmentor = ApplicationContextLocator.getApplicationContext().getBean(Segmentor.class);
        private final Parser parser = ApplicationContextLocator.getApplicationContext().getBean(Parser.class);

        @Override
        public void run() {
            String productUrl;

            parser.setProductBlock(segmentor.setProductBlock(alcohol).getProductBlock());
            parser.setInfoContainer(segmentor.getInfoContainer());

            productUrl = parser.parseUrl();
            log.info("Now parsing url: {}", productUrl);

            try {
                Alcohol alcohol = alcoholRepositoryService.getByUrl(productUrl);
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
                segmentor.setProductDocument(product).setProductMainContent();
                prepareParsingService(parser);
                alcoholRepositoryService.add(director.makeAlcohol(parser, mainPageUrl, productUrl, alcoholType));

                kafkaMessageSender.sendMessage(
                        ParserApi.WineParsedEvent.newBuilder()
                                .setShopLink(mainPageUrl)
                                .addWines(director.getKafkaMessageBuilder())
                                .build()
                );
                WinestyleParserServiceMetricsCollector.incPublished(parserName);
            }
        }

        private void prepareParsingService(Parser parser) {
            parser.setListDescription(segmentor.getListDescription());
            parser.setLeftBlock(segmentor.getLeftBlock());
            parser.setArticlesBlock(segmentor.getArticlesBlock());
            parser.setDescriptionBlock(segmentor.getDescriptionBlock());
        }
    }
}
