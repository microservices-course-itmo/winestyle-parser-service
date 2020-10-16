package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.SparklingRepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.WineRepositoryService;
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
    private final WineRepositoryService wineRepositoryService;
    private final SparklingRepositoryService sparklingRepositoryService;

    // TODO: возвращать распаршенные записи по конкретной ссылке

    @GetMapping("/wine")
    public ResponseEntity<List<Wine>> getParsedWines() {
        List<Wine> parsedWine = wineRepositoryService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(parsedWine);
    }

    @GetMapping("/wine/{id}")
    public ResponseEntity<Wine> getParsedWine(@PathVariable long id) throws NoEntityException {
        Wine parsedWine = wineRepositoryService.getByID(id);
        return ResponseEntity.status(HttpStatus.OK).body(parsedWine);
    }

    @GetMapping("/sparkling")
    public ResponseEntity<List<Sparkling>> getParsedSparkling() {
        List<Sparkling> parsedSparkling = sparklingRepositoryService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(parsedSparkling);
    }

    @GetMapping("/sparkling/{id}")
    public ResponseEntity<Sparkling> getParsedSparkling(@PathVariable long id) throws NoEntityException {
        Sparkling parsedSparkling = sparklingRepositoryService.getByID(id);
        return ResponseEntity.status(HttpStatus.OK).body(parsedSparkling);
    }

    /**
     * @param id id вина в базе данных.
     * @param fieldsList список запрашиваемых полей.
     * @return HTTP-статус 200(ОК) и вино с запрошенными полями в теле ответа.
     * @throws NoEntityException если искомое вино не найдено.
     */
    @GetMapping("/wine/with_fields/{id}")
    public ResponseEntity<Map<String, Object>> getParsedWineWithFields(@PathVariable long id,
            @RequestParam String fieldsList) throws NoEntityException {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(fieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        Wine parsedWine = wineRepositoryService.getByID(id);
        String fieldName;
        for (java.lang.reflect.Field field : Wine.class.getDeclaredFields()) {
            field.setAccessible(true);
            fieldName = field.getName();
            if (requiredFields.contains(fieldName)) {
                try {
                    res.put(fieldName, field.get(parsedWine));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // TODO: обрабатывать исключения
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/sparkling/with_fields/{id}")
    public ResponseEntity<Map<String, Object>> getParsedSparklingWithFields(@PathVariable long id,
                                                                       @RequestParam String fieldsList) throws NoEntityException {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(fieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        Sparkling parsedSparkling = sparklingRepositoryService.getByID(id);
        String fieldName;
        for (java.lang.reflect.Field field : Sparkling.class.getDeclaredFields()) {
            field.setAccessible(true);
            fieldName = field.getName();
            if (requiredFields.contains(fieldName)) {
                try {
                    res.put(fieldName, field.get(parsedSparkling));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
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
                CSVUtility.toCsvFile(wineRepositoryService);
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

    @GetMapping(value = "/sparkling/csv")
    public void getSparklingFile(HttpServletResponse response) {
        File file = new File("data.csv");
        if (!file.exists()) {
            try {
                CSVUtility.toCsvFile(wineRepositoryService);
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
