package com.wine.to.up.winestyle.parser.service.utility;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.service.IWineService;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Класс, сохраняющий все распаршенные вина в csv-файл data.csv.
 */
@Slf4j
public class CSVUtility implements ICSVUtility {
    public void toCsvFile(IWineService wineService) throws IOException {
        List<Wine> wines = wineService.getAllWines();
        try (PrintWriter writer = new PrintWriter("data.csv")) {
            StatefulBeanToCsv<Wine> winecsv = new StatefulBeanToCsvBuilder<Wine>(writer)
                    .withMappingStrategy(new HeaderColumnNameMappingStrategy<>())
                    .build();
            try {
                winecsv.write(wines);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                log.error("Error while csv writing! ", e);
            }
        }

    }
}
