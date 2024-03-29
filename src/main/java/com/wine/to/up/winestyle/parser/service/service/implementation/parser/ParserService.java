package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.logging.NotableEvents;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import com.wine.to.up.winestyle.parser.service.service.implementation.parser.job.MainJob;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParserService implements WinestyleParserService {
    private final MainJob mainJob;
    private final Scraper scraper;

    private static final String PARSING_PROCESS_DURATION_SUMMARY = "parsing_process_duration";
    
    @Value("${spring.jsoup.pagination.css.query.main-bottom}")
    private String paginationElementCssQuery;

    @Value("${spring.jsoup.scraping.winestyle-main-msk-url}")
    private String mskUrl;
    @Value("${spring.jsoup.scraping.winestyle-main-spb-url}")
    private String spbUrl;
    @Value("${spring.jsoup.scraping.winestyle-wine-part-url}")
    private String wineUrl;
    @Value("${spring.jsoup.scraping.winestyle-sparkling-part-url}")
    private String sparklingUrl;

    private ImmutableMap<City, String> supportedCityUrls;
    private ImmutableMap<AlcoholType, String> supportedAlcoholUrls;

    @SuppressWarnings("unused")
    @InjectEventLogger
    private EventLogger eventLogger;

    @PostConstruct
    private void populateUrl() {
        supportedCityUrls = ImmutableMap.<City, String>builder()
                .put(City.MSK, mskUrl)
                .put(City.SPB, spbUrl)
                .build();
        supportedAlcoholUrls = ImmutableMap.<AlcoholType, String>builder()
                .put(AlcoholType.WINE, wineUrl)
                .put(AlcoholType.SPARKLING, sparklingUrl)
                .build();
    }

    @Timed(PARSING_PROCESS_DURATION_SUMMARY)
    @Override
    public void parseBuildSave(AlcoholType alcoholType, City city) throws InterruptedException {

        LocalDateTime start = LocalDateTime.now();
        
        String mainPageUrl = supportedCityUrls.get(city);
        String alcoholUrlPart = supportedAlcoholUrls.get(alcoholType);
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
                    Integer currentUnparsed = mainJob.get(currentDoc, alcoholType, mainPageUrl, start, city);
                    unparsed += currentUnparsed;
                } catch (Exception e) {
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

            mainJob.setParsed(0);
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
}
