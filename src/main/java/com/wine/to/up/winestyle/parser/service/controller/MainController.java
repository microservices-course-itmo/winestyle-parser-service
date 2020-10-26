package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.utility.CSVUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class MainController {
    private final RepositoryService alcoholRepositoryService;

    @GetMapping("/alcohol")
    public List<Alcohol> getAlcohol() {
        log.info("Returned all alcohol via GET /winestyle/api/alcohol");
        return alcoholRepositoryService.getAll();
    }

    @GetMapping("/wines")
    public List<Alcohol> getWines() {
        log.info("Returned all wines via GET /winestyle/api/wines");
        return alcoholRepositoryService.getAllWines();
    }

    @GetMapping("/sparkling")
    public List<Alcohol> getSparkling() {
        log.info("Returned all sparkling via GET /winestyle/api/sparkling");
        return alcoholRepositoryService.getAllSparkling();
    }

    @GetMapping("/alcohol/by-url/{url}")
    public Alcohol getAlcoholByUrl(@PathVariable String url) throws NoEntityException {
        log.info("Returned alcohol with url={} via GET /winestyle/api/alcohol/by-url/{}", url, url);
        return alcoholRepositoryService.getByUrl("/products/" + url);
    }

    @GetMapping("/alcohol/{id}")
    public Alcohol getAlcoholById(@PathVariable long id) throws NoEntityException {
        log.info("Returned alcohol with id={} via GET /winestyle/api/alcohol/{}", id, id);
        return alcoholRepositoryService.getByID(id);
    }

    /**
     * @param id id алкоголя в базе данных.
     * @param fieldsList список запрашиваемых полей.
     * @return HTTP-статус 200(ОК) и алкоголь с запрошенными полями в теле ответа.
     * @throws NoEntityException если искомая позиция не найдена.
     */
    @GetMapping("/alcohol/with-fields/{id}")
    public Map<String, Object> getAlcoholWithFields(@PathVariable long id,
            @RequestParam String fieldsList) throws NoEntityException {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(fieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        Alcohol alcohol = alcoholRepositoryService.getByID(id);
        String fieldName;
        for (java.lang.reflect.Field field : Alcohol.class.getDeclaredFields()) {
            field.setAccessible(true);
            fieldName = field.getName();
            if (requiredFields.contains(fieldName)) {
                try {
                    res.put(fieldName, field.get(alcohol));
                } catch (IllegalArgumentException | IllegalAccessException ignore) { }
            }
        }
        log.info("Returned alcohol with id={} with requested fields ({}) via GET /winestyle/api/alcohol/with-fields/{}", id, fieldsList, id);
        return res;
    }

    @GetMapping(value = "/alcohol/csv")
    public void getAlcoholFile(HttpServletResponse response) {
        File file = new File("data.csv");
        if (!file.exists()) {
            try {
                CSVUtility.toCsvFile(alcoholRepositoryService);
            } catch (IOException e) {
                log.error("Cannot write database to file (GET /winestyle/api/alcohol/csv)");
                throw new RuntimeException("Cannot write database to file");
            }
        }
        try (InputStream is = new FileInputStream(file)) {
            response.setContentType("Content-Disposition: attachment; filename=\"alcohol.csv\"");
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            log.info("Successfully dumped the database and returned csv (GET /winestyle/api/wine/csv)");
        } catch (IOException ex) {
            log.error("Cannot write feeding database csv to outputStream (GET /winestyle/api/wine/csv)");
            throw new RuntimeException("Error while feeding file to outputStream");
        }
    }
}
