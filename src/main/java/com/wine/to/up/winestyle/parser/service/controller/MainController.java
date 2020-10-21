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

    @GetMapping("/alcohol")
    public List<Alcohol> getParsedAlcohol() {
        return alcoholRepositoryService.getAll();
    }

    @GetMapping("/alcohol/{id}")
    public Alcohol getParsedAlcoholById(@PathVariable long id) throws NoEntityException {
        return alcoholRepositoryService.getByID(id);
    }

    @GetMapping("/wines")
    public List<Alcohol> getParsedWines() {
        return alcoholRepositoryService.getAllWines();
    }

    @GetMapping("/sparkling")
    public List<Alcohol> getParsedSparkling() {
        return alcoholRepositoryService.getAllSparkling();
    }

    @GetMapping("/alcohol/by-url/{url}")
    public ResponseEntity<Alcohol> getParsedWineByURL(@PathVariable String url) throws NoEntityException {
        Alcohol parsedAlcohol = alcoholRepositoryService.getByUrl(url);
        return ResponseEntity.status(HttpStatus.OK).body(parsedAlcohol);
    }

    /**
     * @param id id алкоголя в базе данных.
     * @param fieldsList список запрашиваемых полей.
     * @return HTTP-статус 200(ОК) и алкоголь с запрошенными полями в теле ответа.
     * @throws NoEntityException если искомая позиция не найдено.
     */
    @GetMapping("/alcohol/with_fields/{id}")
    public Map<String, Object> getParsedWineWithFields(@PathVariable long id,
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
                }
            }
        }
        return res;
    }

    @GetMapping(value = "/alcohol/csv")
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
            response.setContentType("Content-Disposition: attachment;filename=\"alcohols.csv\"");
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("Error while feading file to outputStream");
        }
    }
}
