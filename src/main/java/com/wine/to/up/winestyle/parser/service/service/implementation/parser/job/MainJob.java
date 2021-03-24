package com.wine.to.up.winestyle.parser.service.service.implementation.parser.job;

import com.wine.to.up.commonlib.annotations.InjectEventLogger;
import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.logging.NotableEvents;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.MainPageSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Component
public class MainJob {
    private final Parser parser;
    private final ProductJob productJob;
    private final ProductUrlJob productUrlJob;
    private final MainPageSegmentor mainPageSegmentor;

    @Value("${spring.jsoup.scraping.interval.millis}")
    private int timeout;
    @Setter
    private int parsed = 0;

    @SuppressWarnings("unused")
    @InjectEventLogger
    private EventLogger eventLogger;

    /**
     * Парсер страницы с позициями
     */
    @SneakyThrows
    public Integer get(Document currentDoc, AlcoholType alcoholType, String mainPageUrl, LocalDateTime start, City city) {
        LocalDateTime mainParsingStart = LocalDateTime.now();
        Elements productElements = mainPageSegmentor.extractProductElements(currentDoc);

        int parsedNow = 0;
        int unparsed = 0;

        String productUrl;
        Alcohol result;

        for (Element productElement : productElements) {
            try {
                productUrl = productUrlJob.get(parser, productElement);
                try {
                    result = productJob.getParsedAlcohol(parser, mainPageUrl, productUrl, productElement, alcoholType, city);
                    eventLogger.info(NotableEvents.I_WINE_DETAILS_PARSED, productUrl, result);
                    parsedNow += 1;
                } catch (Exception e) {
                    eventLogger.warn(NotableEvents.W_WINE_DETAILS_PARSING_FAILED, productUrl);
                    unparsed += 1;
                }
                Thread.sleep(timeout);
            } catch (Exception e) {
                log.error("Critical error during execution of url from product block {}", productElement.html());
            }
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
}