package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlcoholParsingScheduler {
    private final ParsingControllerService parsingControllerService;

    @Value("${spring.task.scheduling.rate.parser.delay}")
    private int timeout;

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholSpbWine() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.SPB, AlcoholType.WINE);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(timeout);
            onScheduleParseAlcoholSpbWine();
        }
    }

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholSpbSparkling() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.SPB, AlcoholType.SPARKLING);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(timeout);
            onScheduleParseAlcoholSpbSparkling();
        }
    }

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholMskWine() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.MSK, AlcoholType.WINE);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(timeout);
            onScheduleParseAlcoholMskWine();
        }
    }

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholMskSparkling() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.MSK, AlcoholType.SPARKLING);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(timeout);
            onScheduleParseAlcoholMskSparkling();
        }
    }
}
