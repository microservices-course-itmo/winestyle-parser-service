package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.MainControllerService;
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
    private final MainControllerService mainControllerService;

    @Value("${spring.jsoup.connection.timeout}")
    private int timeout;

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}")
    public void onScheduleParseWine() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.SPB, AlcoholType.WINE);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseWine();
        }
    }

    @Scheduled(cron = "${spring.task.scheduling.rate.parser.cron}")
    public void onScheduleParseSparkling() throws InterruptedException {
        try {
            parsingControllerService.startParsingJob(City.SPB, AlcoholType.SPARKLING);
        } catch (ServiceIsBusyException e) {
            Thread.sleep(3600000);
            onScheduleParseSparkling();
        }
    }

    @Scheduled(fixedRateString = "${spring.task.scheduling.rate.proxy.fixed}")
    public void onScheduleLoadProxies() {
        try {
            mainControllerService.startProxiesInitJob(timeout);
        } catch (ServiceIsBusyException e) {
            log.info("Scheduled proxy initialization job is rejected. {}", e.getMessage());
        }
    }
}
