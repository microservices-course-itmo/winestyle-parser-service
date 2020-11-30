package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер, который позволяет начать парсинг данного раздела сайта.
 * Как правило используется api/parse/wine.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/winestyle/api/parse")
@Slf4j
public class ParsingController {
    private final ParsingControllerService parsingControllerService;

    /**
     * @param city    город, в котором будет парситься ассортимент
     * @param alcohol тип алкоголя для парсинга (wine или sparkling).
     * @return HTTP-статус 200(ОК) и сообщение о начале парсинга в теле ответа.
     * @throws ServiceIsBusyException когда парсинг уже запущен.
     */
    @PostMapping("/{city}/{alcohol}")
    public ResponseEntity<String> startParsing(@PathVariable City city, @PathVariable AlcoholType alcohol) throws ServiceIsBusyException {
        parsingControllerService.startParsingJob(city, alcohol);
        return new ResponseEntity<>("Parsing job was successfully launched.", HttpStatus.OK);
    }
}
