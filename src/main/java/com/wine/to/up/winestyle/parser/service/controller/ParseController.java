package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.ParserService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@RequestMapping("/winestyle/api/parse")
public class ParseController {
    private final ParserService parserService;

    /**
     * @param alcohol тип алкоголя для парсинга(wine).
     * @return HTTP-статус 200(ОК) и сообщение о начале парсинга в теле ответа.
     * @throws ServiceIsBusyException когда парсинг уже запущен.
     */
    @PostMapping("/{alcohol}")
    public ResponseEntity<String> startParsing(@PathVariable String alcohol) throws ServiceIsBusyException {
        parserService.startParsingJob(alcohol);
        return new ResponseEntity<>("Parsing job was successfully launched.", HttpStatus.OK);
    }
}
