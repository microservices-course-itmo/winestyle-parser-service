package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlcoholParsingScheduler {
    private final ParsingControllerService parsingControllerService;

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholSpbWine() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.SPB, AlcoholType.WINE);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseAlcoholSpbWine();
        }
    }

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholSpbSparkling() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.SPB, AlcoholType.SPARKLING);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseAlcoholSpbSparkling();
        }
    }

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholMskWine() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.MSK, AlcoholType.WINE);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseAlcoholMskWine();
        }
    }

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}", zone = "EAT")
    public void onScheduleParseAlcoholMskSparkling() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.MSK, AlcoholType.SPARKLING);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseAlcoholMskSparkling();
        }
    }
}
