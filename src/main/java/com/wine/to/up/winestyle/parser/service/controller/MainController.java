package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.IllegalFieldException;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.MainControllerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private final MainControllerService mainControllerService;
    private final RepositoryService alcoholRepositoryService;

    @GetMapping(value = "/alcohol", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Alcohol> getAlcohol() {
        log.info("Returned all alcohol via GET /winestyle/api/alcohol");
        return alcoholRepositoryService.getAll();
    }

    @GetMapping(value = "/wines", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Alcohol> getWines() {
        log.info("Returned all wines via GET /winestyle/api/wines");
        return alcoholRepositoryService.getAllWines();
    }

    @GetMapping(value = "/sparkling", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Alcohol> getSparkling() {
        log.info("Returned all sparkling via GET /winestyle/api/sparkling");
        return alcoholRepositoryService.getAllSparkling();
    }

    @GetMapping(value = "/alcohol/by-url/{url}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Alcohol getAlcoholByUrl(@PathVariable String url) throws NoEntityException {
        log.info("Returned alcohol with url={} via GET /winestyle/api/alcohol/by-url/{}", url, url);
        return alcoholRepositoryService.getByUrl("/products/" + url);
    }

    @GetMapping(value = "/alcohol/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Alcohol getAlcoholById(@PathVariable long id) throws NoEntityException {
        log.info("Returned alcohol with id={} via GET /winestyle/api/alcohol/{}", id, id);
        return alcoholRepositoryService.getByID(id);
    }

    /**
     * @param id         id алкоголя в базе данных.
     * @param fieldsList список запрашиваемых полей.
     * @return HTTP-статус 200(ОК) и алкоголь с запрошенными полями в теле ответа.
     * @throws NoEntityException если искомая позиция не найдена.
     */
    @GetMapping(value = "/alcohol/with-fields/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getAlcoholWithFields(@PathVariable long id, @RequestParam String fieldsList)
            throws NoEntityException, IllegalFieldException {
        return mainControllerService.getAlcoholWithFields(id, fieldsList);
    }

    @GetMapping(value = "/alcohol/csv")
    public void getAlcoholFile(HttpServletResponse response) {
        mainControllerService.getAlcoholFile(response);
    }
}
