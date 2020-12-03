package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Timing;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * Класс-парсер.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingControllerService {
    private final WinestyleParserService alcoholParserService;
    private final StatusService statusService;
    private final RepositoryService repositoryService;

    @Value("${spring.kafka.metrics.service-name}")
    private String parserName;

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

    // Start parsing job in a separate thread
    public void startParsingJob(City city, AlcoholType alcoholType) throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.PARSER)) {
            log.info("Started parsing of {} via /winestyle/api/parse/{}/{}", alcoholType, city, alcoholType);
            WinestyleParserServiceMetricsCollector.incParsingStarted();
            WinestyleParserServiceMetricsCollector.gaugeInProgress(1);
            new Thread(() -> {
                alcoholParserService.setAlcoholType(alcoholType);
                alcoholParserService.setMainPageUrl(supportedCityUrls.get(city));
                try {
                    alcoholParserService.parseBuildSave(supportedAlcoholUrls.get(alcoholType));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    repositoryService.add(new Timing(LocalDateTime.now()));
                    WinestyleParserServiceMetricsCollector.incParsingComplete();
                    WinestyleParserServiceMetricsCollector.gaugeInProgress(0);
                    statusService.release(ServiceType.PARSER);
                }
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("parsing job is already running");
        }
    }
}
