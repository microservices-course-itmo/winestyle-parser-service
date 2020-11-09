package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.controller.exception.UnsupportedAlcoholTypeException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.MainControllerService;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlcoholParsingScheduler {
    private final ParsingControllerService parsingControllerService;
    private final MainControllerService mainControllerService;

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

    @Scheduled(fixedRate = 3600000)
    public void onScheduleLoadProxies() {
        try {
            mainControllerService.startProxyInitJob(20000);
        } catch (ServiceIsBusyException e) {
            log.info("Scheduled proxy initialization job is rejected. {}", e.getMessage());
        }
    }
}
