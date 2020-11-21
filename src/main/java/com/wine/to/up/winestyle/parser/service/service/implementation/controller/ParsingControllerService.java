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

    @Value("${spring.jsoup.scraping.winestyle-wine-part-url}")
    private String WINE_URL;
    @Value("${spring.jsoup.scraping.winestyle-sparkling-part-url}")
    private String SPARKLING_URL;

    private ImmutableMap<AlcoholType, String> SUPPORTED_ALCOHOL_URLS;

    @PostConstruct
    private void populateUrl() {
        SUPPORTED_ALCOHOL_URLS = ImmutableMap.<AlcoholType, String>builder()
                .put(AlcoholType.WINE, WINE_URL)
                .put(AlcoholType.SPARKLING, SPARKLING_URL)
                .build();
    }

    // Start parsing job in a separate thread
    public void startParsingJob(AlcoholType alcoholType) throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.PARSER)) {
            log.info("Started parsing of {} via /winestyle/api/parse/{}", alcoholType, alcoholType);
            new Thread(() -> {
                try {
                    alcoholParserService.parseBuildSave(SUPPORTED_ALCOHOL_URLS.get(alcoholType), alcoholType);
                } finally {
                    statusService.release(ServiceType.PARSER);
                }
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("parsing job is already running");
        }
    }
}
