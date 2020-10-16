package com.wine.to.up.winestyle.parser.service.utility;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;

import com.wine.to.up.winestyle.parser.service.service.implementation.repository.RepositoryService;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Класс, сохраняющий все распаршенные вина в csv-файл data.csv.
 */
@UtilityClass
@Slf4j
public class CSVUtility {
    public void  toCsvFile(RepositoryService repositoryService) throws IOException {
        List<Wine> wines = repositoryService.getAll();
        try (PrintWriter writer = new PrintWriter("data.csv")) {
            HeaderColumnNameMappingStrategy<Wine> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Wine.class);
            StatefulBeanToCsv<Wine> winecsv = new StatefulBeanToCsvBuilder<Wine>(writer)
                    .withMappingStrategy(strategy)
                    .build();
            try {
                winecsv.write(wines);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                log.error("Error while csv writing! ", e);
            }
        }
    }
}
