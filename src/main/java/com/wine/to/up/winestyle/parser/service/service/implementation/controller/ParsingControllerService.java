package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

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
import org.springframework.stereotype.Service;

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

    // Start parsing job in a separate thread
    public void startParsingJob(City city, AlcoholType alcoholType) throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.PARSER)) {
            log.info("Started parsing of {} via /winestyle/api/parse/{}/{}", alcoholType, city, alcoholType);
            WinestyleParserServiceMetricsCollector.incParsingStarted();
            WinestyleParserServiceMetricsCollector.updateInProgress(1);
            new Thread(() -> {
                try {
                    alcoholParserService.parseBuildSave(alcoholType, city);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    repositoryService.add(new Timing(LocalDateTime.now()));
                    WinestyleParserServiceMetricsCollector.incParsingComplete();
                    WinestyleParserServiceMetricsCollector.updateInProgress(0);
                    statusService.release(ServiceType.PARSER);
                }
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("parsing job is already running");
        }
    }
}
