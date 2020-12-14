package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.IllegalFieldException;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.MainControllerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Контроллер, возвращающий результаты парсинга
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/winestyle/api")
@Slf4j
public class MainController {
    private final MainControllerService mainControllerService;
    private final RepositoryService repositoryService;

    /**
     * Получение списка всего алкоголя.
     *
     * @return полный список алкоголя.
     */
    @GetMapping(value = "/alcohol", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Alcohol> getAlcohol() {
        log.info("Returned all alcohol via GET /winestyle/api/alcohol");
        return repositoryService.getAll();
    }

    /**
     * Получение списка всех вин.
     *
     * @return полный список вин.
     */
    @GetMapping(value = "/wines", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Alcohol> getWines() {
        log.info("Returned all wines via GET /winestyle/api/wines");
        return repositoryService.getAllWines();
    }

    /**
     * Получение списка всего шампанского.
     *
     * @return полный список шампанского.
     */
    @GetMapping(value = "/sparkling", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Alcohol> getSparkling() {
        log.info("Returned all sparkling via GET /winestyle/api/sparkling");
        return repositoryService.getAllSparkling();
    }

    /**
     * Получение алкоголя по url.
     *
     * @param url ссылка на алкоголь.
     * @return сущность алкоголя.
     * @throws NoEntityException
     */
    @GetMapping(value = "/alcohol/by-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public Alcohol getAlcoholByUrl(@RequestParam String url) throws NoEntityException {
        log.info("Returned alcohol with url={} via GET /winestyle/api/alcohol/by-url/{}", url, url);
        return repositoryService.getByUrl("/products/" + url);
    }

    /**
     * Получение алкоголя по id.
     *
     * @param id id нужного алкоголя.
     * @return сущность алкоголя.
     * @throws NoEntityException
     */
    @GetMapping(value = "/alcohol/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Alcohol getAlcoholById(@PathVariable long id) throws NoEntityException {
        log.info("Returned alcohol with id={} via GET /winestyle/api/alcohol/{}", id, id);
        return repositoryService.getByID(id);
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

    /**
     * Получение csv файла алкоголя.
     *
     * @param response файл алкоголя.
     */
    @GetMapping(value = "/alcohol/csv")
    public void getAlcoholFile(HttpServletResponse response) {
        mainControllerService.getAlcoholFile(response);
    }

    /**
     * Проверка проксей.
     *
     * @param maxTimeout максимальное время ожидания прокси.
     * @return ответ о запуске.
     * @throws ServiceIsBusyException
     */
    @PostMapping("/proxy/init")
    public ResponseEntity<String> initProxies(@RequestParam int maxTimeout) throws ServiceIsBusyException {
        mainControllerService.startProxiesInitJob(maxTimeout);
        return new ResponseEntity<>("Proxy initialization job was successfully launched.", HttpStatus.OK);
    }
}
