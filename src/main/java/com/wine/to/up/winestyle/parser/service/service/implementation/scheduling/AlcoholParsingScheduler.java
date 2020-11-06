package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.controller.exception.UnsupportedAlcoholTypeException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlcoholParsingScheduler {
    private final ParsingControllerService parsingControllerService;

    @SneakyThrows(UnsupportedAlcoholTypeException.class)
    @Scheduled(cron = "${scheduler.cron.expression}")
    public void onScheduleParseWine() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob("wine");
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseWine();
        }
    }

    @SneakyThrows(UnsupportedAlcoholTypeException.class)
    @Scheduled(cron = "${scheduler.cron.expression}")
    public void onScheduleParseSparkling() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob("sparkling");
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseSparkling();
        }
    }
}
