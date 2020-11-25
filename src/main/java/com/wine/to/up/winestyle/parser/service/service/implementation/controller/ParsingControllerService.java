package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Класс-парсер.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingControllerService {
    private final WinestyleParserService alcoholParserService;
    private final StatusService statusService;

    @Value("${spring.jsoup.scraping.winestyle-main-msk-url}")
    private String MSK_URL;
    @Value("${spring.jsoup.scraping.winestyle-main-spb-url}")
    private String SPB_URL;
    @Value("${spring.jsoup.scraping.winestyle-wine-part-url}")
    private String WINE_URL;
    @Value("${spring.jsoup.scraping.winestyle-sparkling-part-url}")
    private String SPARKLING_URL;

    private ImmutableMap<City, String> SUPPORTED_CITY_URLS;
    private ImmutableMap<AlcoholType, String> SUPPORTED_ALCOHOL_URLS;

    @PostConstruct
    private void populateUrl() {
        SUPPORTED_CITY_URLS = ImmutableMap.<City, String>builder()
                .put(City.MSK, MSK_URL)
                .put(City.SPB, SPB_URL)
                .build();
        SUPPORTED_ALCOHOL_URLS = ImmutableMap.<AlcoholType, String>builder()
                .put(AlcoholType.WINE, WINE_URL)
                .put(AlcoholType.SPARKLING, SPARKLING_URL)
                .build();
    }

    // Start parsing job in a separate thread
    public void startParsingJob(City city, AlcoholType alcoholType) throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.PARSER)) {
            log.info("Started parsing of {} via /winestyle/api/parse/{}", alcoholType, alcoholType);
            new Thread(() -> {
                try {
                    alcoholParserService.parseBuildSave(SUPPORTED_CITY_URLS.get(city),
                            SUPPORTED_ALCOHOL_URLS.get(alcoholType), alcoholType);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    statusService.release(ServiceType.PARSER);
                }
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("parsing job is already running");
        }
    }
}
