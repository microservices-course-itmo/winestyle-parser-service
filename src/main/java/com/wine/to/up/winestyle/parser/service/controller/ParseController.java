package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.IParserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/parse")
public class ParseController {
    private final IParserService parserService;

    @PostMapping("/{alcohol}")
    public ResponseEntity<String> startParsing(@PathVariable String alcohol) throws ServiceIsBusyException {
        parserService.startParsingJob(alcohol);
        return new ResponseEntity<>("Parsing job was successfully launched.", HttpStatus.OK);
    }
}
