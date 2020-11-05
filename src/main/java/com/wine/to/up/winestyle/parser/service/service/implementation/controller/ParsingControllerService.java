package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.UnsupportedAlcoholTypeException;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс-парсер.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingControllerService {
    private final WinestyleParserService alcoholParserService;
    private final StatusService statusService;
    private ExecutorService parsingExecutor;

    private final ImmutableMap<String, String> SUPPORTED_ALCOHOL_URLS = ImmutableMap.<String, String>builder()
            .put("wine", "/wine/all/")
            .put("sparkling", "/champagnes-and-sparkling/champagnes/sparkling/sparkling-blue_ll/")
            .build();

    // Start parsing job in a separate thread
    public void startParsingJob(String alcoholType) throws ServiceIsBusyException, UnsupportedAlcoholTypeException {
        String alcoholUrl = SUPPORTED_ALCOHOL_URLS.get(alcoholType);
        if (alcoholUrl != null) {
            if (statusService.tryBusy()) {
                parsingExecutor = Executors.newSingleThreadExecutor();
                parsingExecutor.submit(() -> {
                    try {
                        parse(alcoholUrl, alcoholType);
                    } catch (InterruptedException ignore) {
                    } finally {
                        statusService.release();
                        parsingExecutor.shutdown();
                    }
                });
            } else {
                throw ServiceIsBusyException.createWith("parsing job is already running");
            }
        } else {
            throw UnsupportedAlcoholTypeException.createWith("is not supported", alcoholType);
        }
    }

    private void parse(String relativeUrl, String alcoholType) throws InterruptedException {
        log.info("Started parsing of {} via /winestyle/api/parse/{}", alcoholType, alcoholType);
        String mainUrl = "https://spb.winestyle.ru";
        alcoholParserService.parseBuildSave(mainUrl, relativeUrl, alcoholType);
    }
}
