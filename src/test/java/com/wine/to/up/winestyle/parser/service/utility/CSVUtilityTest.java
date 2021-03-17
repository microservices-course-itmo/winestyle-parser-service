package com.wine.to.up.winestyle.parser.service.utility;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class CSVUtilityTest {
    CSVUtility csvUtility = mock(CSVUtility.class);
    String testCsvFilePath = "testCsvFile.csv";

    @Test
    void toCsvFile() throws IOException {
        RepositoryService repositoryService = mock(RepositoryService.class);
        Alcohol testAlcohol =  Alcohol.builder()
                .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color("Красное").country("test").region("test")
                .volume(1F).strength(1F).sugar("Сухое").price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();
        List<Alcohol> testAlcoholList = List.of(testAlcohol, testAlcohol);
        Mockito.when(repositoryService.getAll()).thenReturn(testAlcoholList);
        ReflectionTestUtils.setField(csvUtility, "filename", testCsvFilePath);
        csvUtility.toCsvFile(repositoryService);
        File file = new File(testCsvFilePath);
        assertTrue(file.delete());
    }
}