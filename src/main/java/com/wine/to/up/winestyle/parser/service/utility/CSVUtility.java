package com.wine.to.up.winestyle.parser.service.utility;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Класс, сохраняющий все распаршенные вина в csv-файл data.csv.
 */
@UtilityClass
@Slf4j
public class CSVUtility {
    public void toCsvFile(RepositoryService repositoryService) throws IOException {
        List<Alcohol> alcohol = repositoryService.getAll();
        try (OutputStream os = new FileOutputStream("data.csv");
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            HeaderColumnNameMappingStrategy<Alcohol> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Alcohol.class);
            StatefulBeanToCsv<Alcohol> alcoholCsv = new StatefulBeanToCsvBuilder<Alcohol>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            try {
                alcoholCsv.write(alcohol);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                log.error("Error while csv writing! ", e);
            }
        }
    }
}
