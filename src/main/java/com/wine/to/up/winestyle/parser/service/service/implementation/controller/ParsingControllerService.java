package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.UnsupportedAlcoholTypeException;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Класс-парсер.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingControllerService {
    private final WinestyleParserService wineParserService;
    private final WinestyleParserService sparklingParserService;
    private final StatusService statusService;

    private final ImmutableMap<String, String> SUPPORTED_ALCOHOL_URLS = ImmutableMap.<String, String>builder()
            .put("wine", "/wine/wines_ll/")
            .put("sparkling", "/champagnes-and-sparkling/all/")
            .build();

    // Start parsing job in a separate thread
    public void startParsingJob(String alcoholType) throws ServiceIsBusyException, UnsupportedAlcoholTypeException {
        String alcoholUrl = SUPPORTED_ALCOHOL_URLS.get(alcoholType);
        if (alcoholUrl != null) {
            if (statusService.statusCheck(alcoholType)) {
                Thread newThread = new Thread(() -> {
                    try {
                        parse(alcoholUrl, alcoholType);
                    } catch (InterruptedException e) {
                        log.error("Thread is sleeping!", e);
                    }
                });
                newThread.start();
            } else {
                throw ServiceIsBusyException.createWith(" parsing job is already running.", alcoholType);
            }
        } else {
            throw UnsupportedAlcoholTypeException.createWith("is not supported.", alcoholType);
        }
    }

    // At 00:00; every day
    @Scheduled(cron = "${scheduler.cron.expression}")
    public void onScheduleParseWinePages() {
        if (statusService.statusCheck("wine")) {
            try {
                parse(SUPPORTED_ALCOHOL_URLS.get("wine"), "wine");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // At 00:00; every day
    @Scheduled(cron = "${scheduler.cron.expression}")
    public void onScheduleParseSparklingPages() {
        if (statusService.statusCheck("sparkling")) {
            try {
                parse(SUPPORTED_ALCOHOL_URLS.get("sparkling"), "sparkling");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void parse(String relativeUrl, String alcoholType) throws InterruptedException {
        statusService.statusChange(alcoholType);

        String mainUrl = "https://spb.winestyle.ru";

        switch (alcoholType) {
            case "wine":
                wineParserService.parseBuildSave(mainUrl, relativeUrl);
                break;
            case "sparkling":
                sparklingParserService.parseBuildSave(mainUrl, relativeUrl);
                break;
        }

        statusService.statusChange(alcoholType);
    }
}
