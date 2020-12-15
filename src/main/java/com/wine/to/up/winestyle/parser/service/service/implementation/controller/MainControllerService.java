package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.AlcoholRepositoryService;
import com.wine.to.up.winestyle.parser.service.utility.CSVUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainControllerService {
    private final AlcoholRepositoryService alcoholRepositoryService;

    public Map<String, Object> getAlcoholWithFields(long id, String fieldsList)
            throws NoEntityException {
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
                } catch (IllegalAccessException e) {
                    log.error("Requested {} field is inaccessible", field.getName());
                }
            }
        }
        log.info("Returned alcohol with id={} with requested fields ({}) via GET /winestyle/api/alcohol/with-fields/{}",
                id, fieldsList, id);
        return res;
    }

    public void getAlcoholFile(HttpServletResponse response) {
        response.setHeader("Content-Type", "text/csv; charset=utf-8");
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
