package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderScheduler {
    private final KafkaService kafkaService;

    @Async
    @Scheduled(fixedRateString = "${spring.task.scheduling.rate.auxiliary.fixed.millis}")
    public void onScheduleSendAlcoholKafka() {
        kafkaService.sendAllWines();
    }
}
