package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.UnsupportedAlcoholTypeException;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Класс-парсер.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParsingControllerService {
    private final WinestyleParserService alcoholParserService;
    private final StatusService statusService;

    private final ImmutableMap<String, String> SUPPORTED_ALCOHOL_URLS = ImmutableMap.<String, String>builder()
            .put("wine", "/wine/all/")
            .put("sparkling", "/champagnes-and-sparkling/champagnes/sparkling/sparkling-blue_ll/")
            .build();

    // Start parsing job in a separate thread
    public void startParsingJob(String alcoholType) throws ServiceIsBusyException, UnsupportedAlcoholTypeException {
        String alcoholUrl = SUPPORTED_ALCOHOL_URLS.get(alcoholType);
        if (alcoholUrl != null) {
            if (statusService.tryBusy(ServiceType.PARSER)) {
                log.info("Started parsing of {} via /winestyle/api/parse/{}", alcoholType, alcoholType);
                new Thread(() -> {
                    try {
                        alcoholParserService.parseBuildSave("https://spb.winestyle.ru", alcoholUrl, alcoholType);
                    } finally {
                        statusService.release(ServiceType.PARSER);
                    }
                }).start();
            } else {
                throw ServiceIsBusyException.createWith("parsing job is already running");
            }
        } else {
            throw UnsupportedAlcoholTypeException.createWith("is not supported", alcoholType);
        }
    }
}
