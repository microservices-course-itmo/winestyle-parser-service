package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.controller.exception.UnsupportedAlcoholTypeException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import lombok.RequiredArgsConstructor;
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
public class ParsingController {
    private final ParsingControllerService parsingControllerService;

    /**
     * @param alcohol тип алкоголя для парсинга(wine).
     * @return HTTP-статус 200(ОК) и сообщение о начале парсинга в теле ответа.
     * @throws ServiceIsBusyException когда парсинг уже запущен.
     */
    @PostMapping("/{alcohol}")
    public ResponseEntity<String> startParsing(@PathVariable String alcohol)
            throws ServiceIsBusyException, UnsupportedAlcoholTypeException {
        parsingControllerService.startParsingJob(alcohol);
        return new ResponseEntity<>("Parsing job was successfully launched.", HttpStatus.OK);
    }
}
