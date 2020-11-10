package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ClientConnectionProblem;
import com.wine.to.up.winestyle.parser.service.controller.exception.FileCreationExeption;
import com.wine.to.up.winestyle.parser.service.controller.exception.IllegalFieldException;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.AlcoholRepositoryService;
import com.wine.to.up.winestyle.parser.service.utility.CSVUtility;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.ScrapingService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainControllerService {
    private final AlcoholRepositoryService alcoholRepositoryService;
    private final StatusService statusService;
    private final ScrapingService scrapingService;

    @SneakyThrows({IntrospectionException.class, InvocationTargetException.class})
    public Map<String, Object> getAlcoholWithFields(long id, String fieldsList)
            throws NoEntityException, IllegalFieldException {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(fieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        PropertyDescriptor pd;
        for (String fieldName : requiredFields) {
            try {
                Alcohol.class.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw IllegalFieldException.createWith("Alcohol", fieldName);
            }
            try {
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Alcohol alcohol = alcoholRepositoryService.getByID(id);
                pd = new PropertyDescriptor(fieldName, Alcohol.class, getterName, null);
                res.put(fieldName, pd.getReadMethod().invoke(alcohol));
            } catch (IllegalAccessException e) {
                log.error("Requested {} field is inaccessible", fieldName);
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
                throw new FileCreationExeption();
            }
        }
        try (InputStream is = new FileInputStream(file)) {
            response.setContentType("Content-Disposition: attachment; filename=\"alcohol.csv\"");
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            log.info("Successfully dumped the database and returned csv (GET /winestyle/api/wine/csv)");
        } catch (IOException ex) {
            log.error("Cannot write feeding database csv to outputStream (GET /winestyle/api/wine/csv)");
            throw new ClientConnectionProblem();
        }
    }


    public void startProxyInitJob(int maxTimeout) throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.PROXY)) {
            new Thread(() -> {
                try {
                    scrapingService.initProxy(maxTimeout);
                } finally {
                    statusService.release(ServiceType.PROXY);
                }
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("proxy initialization job is already running");
        }
    }
}
