package com.wine.to.up.winestyle.parser.service.service.implementation;

import com.wine.to.up.winestyle.parser.service.controller.exception.UnsupportedAlcoholTypeException;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;

import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс-парсер.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WinestyleParserService {
    private final DocumentService documentService;
    private final ParserDirectorService parserDirectorService;

    String mainUrl = "https://spb.winestyle.ru";

    private final ImmutableMap<String, String> SUPPORTED_ALCOHOL_URLS = ImmutableMap.<String, String>builder()
            .put("wine", "/wine/wines_ll/")
            .put("sparkling", "/champagnes-and-sparkling/all/")
            .build();

    private final HashMap<String, Boolean> SERVICE_BUSY_STATUS =
            new HashMap<>(Map.of("wine", false, "sparkling", false));

    // Start parsing job in a separate thread
    public void startParsingJob(String alcoholType) throws ServiceIsBusyException, UnsupportedAlcoholTypeException {
        String alcoholUrl = SUPPORTED_ALCOHOL_URLS.get(alcoholType);
        if (alcoholUrl != null) {
            if (statusCheck(alcoholType)) {
                Thread newThread = new Thread(() -> {
                    try {
                        parseByPages(alcoholUrl, alcoholType);
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
        if (statusCheck("wine")) {
            try {
                parseByPages(SUPPORTED_ALCOHOL_URLS.get("wine"), "wine");
            } catch (InterruptedException e) {
                log.error("Error on schedule with parsing wines pages!", e);
            }
        }
    }

    // At 00:00; every day
    @Scheduled(cron = "${scheduler.cron.expression}")
    public void onScheduleParseSparklingPages() {
        if (statusCheck("sparkling")) {
            try {
                parseByPages(SUPPORTED_ALCOHOL_URLS.get("sparkling"), "sparkling");
            } catch (InterruptedException e) {
                log.error("Error on schedule with parsing wines pages!", e);
            }
        }
    }

    // Page by page parsing
    private void parseByPages(String relativeUrl, String alcoholType) throws InterruptedException {
        statusChange(alcoholType);

        String alcoholMainPageUrl = mainUrl + relativeUrl;
        Document mainDoc = documentService.getJsoupDocument(alcoholMainPageUrl);
        int pages = documentService.pagesNumber(mainDoc);

        Elements productElements;

        for (int i = 2; i <= pages; i++) {
            productElements = mainDoc.getElementsByClass("item-block");
            for (Element productElement : productElements) {
                switch (alcoholType) {
                    case "wine":
                        parserDirectorService.parseWine(productElement);
                        break;
                    case "sparkling":
                        parserDirectorService.parseSparkling(productElement);
                        break;
                }
            }
            mainDoc = documentService.getJsoupDocument(alcoholMainPageUrl + "?page=" + i);
        }
        statusChange(alcoholType);
    }

    private boolean statusCheck(String alcoholType) {
        return !SERVICE_BUSY_STATUS.get(alcoholType);
    }

    private void statusChange(String alcoholType) {
        SERVICE_BUSY_STATUS.replace(alcoholType, !SERVICE_BUSY_STATUS.get(alcoholType));
    }
}
