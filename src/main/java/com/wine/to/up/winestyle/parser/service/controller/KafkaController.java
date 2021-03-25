package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/winestyle/kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaController {
    private final KafkaService kafkaSenderService;

    @PostMapping("/alcohol")
    public void sendAllalcoholToKafka() {
        kafkaSenderService.sendAllAlcohol();
    }

    @PostMapping("/alcohol/wines")
    public void sendAllWinesToKafka() {
        kafkaSenderService.sendAllWines();
    }

    @PostMapping("/alcohol/sparkling")
    public void sendAllSparklingToKafka() {
        kafkaSenderService.sendAllSparkling();
    }
}
