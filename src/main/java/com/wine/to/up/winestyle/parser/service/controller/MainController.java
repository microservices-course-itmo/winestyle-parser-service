package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер, возвращающий результаты парсинга
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class MainController {
    private final WineRepository wineRepository;

    //TODO: возвращать распаршенные записи по конкретной ссылке
    //TODO: возвращать только запрашиваемые столбцы
    @GetMapping("/wine")
    public ResponseEntity<List<Wine>> getParsedWine() {
        List<Wine> parsedWine = wineRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(parsedWine);
    }
}