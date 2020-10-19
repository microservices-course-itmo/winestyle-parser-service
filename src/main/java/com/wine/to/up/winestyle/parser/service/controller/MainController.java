package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.AlcoholRepositoryService;
import com.wine.to.up.winestyle.parser.service.utility.CSVUtility;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

/**
 * Контроллер, возвращающий результаты парсинга
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/winestyle/api")
public class MainController {
    private final AlcoholRepositoryService alcoholRepositoryService;

    @GetMapping("/all")
    public ResponseEntity<List<Alcohol>> getParsedAlcohol() {
        List<Alcohol> parsedAlcohol = alcoholRepositoryService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(parsedAlcohol);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Alcohol> getParsedAlcoholById(@PathVariable long id) throws NoEntityException {
        Alcohol parsedAlcohol = alcoholRepositoryService.getByID(id);
        return ResponseEntity.status(HttpStatus.OK).body(parsedAlcohol);
    }

    @GetMapping("/wine")
    public ResponseEntity<List<Alcohol>> getParsedWines() {
        List<Alcohol> parsedWine = alcoholRepositoryService.getAllWines();
        return ResponseEntity.status(HttpStatus.OK).body(parsedWine);
    }

    @GetMapping("/sparkling")
    public ResponseEntity<List<Alcohol>> getParsedSparkling() {
        List<Alcohol> parsedSparkling = alcoholRepositoryService.getAllSparklings();
        return ResponseEntity.status(HttpStatus.OK).body(parsedSparkling);
    }

    /**
     * @param id id алкоголя в базе данных.
     * @param fieldsList список запрашиваемых полей.
     * @return HTTP-статус 200(ОК) и алкоголь с запрошенными полями в теле ответа.
     * @throws NoEntityException если искомая позиция не найдено.
     */
    @GetMapping("/all/with_fields/{id}")
    public ResponseEntity<Map<String, Object>> getParsedWineWithFields(@PathVariable long id,
            @RequestParam String fieldsList) throws NoEntityException {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(fieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        Alcohol parsedAlcohol = alcoholRepositoryService.getByID(id);
        String fieldName;
        for (java.lang.reflect.Field field : Alcohol.class.getDeclaredFields()) {
            field.setAccessible(true);
            fieldName = field.getName();
            if (requiredFields.contains(fieldName)) {
                try {
                    res.put(fieldName, field.get(parsedAlcohol));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // TODO: обрабатывать исключения
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping(value = "/wine/csv")
    public void getWineFile(HttpServletResponse response) {
        File file = new File("data.csv");
        if (!file.exists()) {
            try {
                CSVUtility.toCsvFile(alcoholRepositoryService);
            } catch (IOException e) {
                throw new RuntimeException("Cannot write database to file");
            }
        }
        try (InputStream is = new FileInputStream(file)) {
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("Error while feading file to outputStream");
        }
    }
}
