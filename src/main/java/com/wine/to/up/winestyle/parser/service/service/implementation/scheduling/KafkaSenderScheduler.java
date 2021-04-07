package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.KafkaSenderControllerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderScheduler {
    private final KafkaSenderControllerService kafkaSenderControllerService;

    @Async
    @Scheduled(fixedDelayString = "${spring.task.scheduling.rate.auxiliary.fixed.millis}")
    public void onScheduleSendAlcoholKafka() {
        try {
            kafkaSenderControllerService.startSendingAlcohol();
        } catch (ServiceIsBusyException ignored) {

        }
    }
}
