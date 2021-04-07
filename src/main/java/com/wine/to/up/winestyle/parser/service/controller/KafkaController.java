package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.KafkaSenderControllerService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер, который позволяет начать отправку позиций алкоголя в Кафку.
 */
@RestController
@RequestMapping("/winestyle/api/kafka")
@Slf4j
@RequiredArgsConstructor
public class KafkaController {
    private final KafkaSenderControllerService kafkaSenderControllerService;

    /**
     * @return HTTP-статус 200(ОК) и сообщение о начале парсинга в теле ответа.
     * @throws ServiceIsBusyException когда отправка уже запущена.
     */
    @PostMapping("/alcohol")
    public String sendAllAlcoholToKafka() throws ServiceIsBusyException {
        kafkaSenderControllerService.startSendingAlcohol();
        return "Sending process of alcohol to kafka was successfully launched.";
    }

    /**
     * @param type тип алкоголя для отправки (wine или sparkling).
     * @return HTTP-статус 200(ОК) и сообщение о начале парсинга в теле ответа.
     * @throws ServiceIsBusyException когда отправка уже запущена.
     */
    @PostMapping("/alcohol/{type}")
    public String sendAllTypeAlcoholToKafka(@PathVariable AlcoholType type) throws ServiceIsBusyException {
        kafkaSenderControllerService.startSendingAlcohol(type);
        return String.format("Sending process of %s to kafka was successfully launched.", type.toString());
    }
}
