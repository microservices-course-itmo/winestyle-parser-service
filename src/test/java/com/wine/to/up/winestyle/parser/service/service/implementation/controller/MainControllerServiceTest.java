package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.IllegalFieldException;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.ApplicationRepositoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerServiceTest {

    @InjectMocks
    private MainControllerService mainControllerService;
    @Mock
    private ApplicationRepositoryService ApplicationRepositoryService;

    static Alcohol alcohol;
    static String fieldsList;
    static String emptyFieldsList;
    static String wrongFieldsList;
    static Map<String, Object> expectedFullMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        alcohol = Alcohol.builder()
                .id(1L).name("test").type("wine").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color("test").country("test").region("test")
                .volume(1F).strength(1F).sugar("test").price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();
        fieldsList = "id,name,type,url,imageUrl,cropYear,manufacturer,brand,color,country,region,volume" +
                ",strength,sugar,price,grape,taste,aroma,foodPairing,description,rating";
        emptyFieldsList = "";
        wrongFieldsList = "name,wrong,bad";

        expectedFullMap = new HashMap<>();
        expectedFullMap.put("id", 1L);
        expectedFullMap.put("name", "test");
        expectedFullMap.put("type", "wine");
        expectedFullMap.put("url", "test");
        expectedFullMap.put("cropYear", 1990);
        expectedFullMap.put("manufacturer", "test");
        expectedFullMap.put("brand", "test");
        expectedFullMap.put("color", "test");
        expectedFullMap.put("country", "test");
        expectedFullMap.put("region", "test");
        expectedFullMap.put("volume", 1F);
        expectedFullMap.put("strength", 1F);
        expectedFullMap.put("sugar", "test");
        expectedFullMap.put("price", 1F);
        expectedFullMap.put("imageUrl", "test");
        expectedFullMap.put("grape", "test");
        expectedFullMap.put("taste", "test");
        expectedFullMap.put("aroma", "test");
        expectedFullMap.put("foodPairing", "test");
        expectedFullMap.put("description", "test");
        expectedFullMap.put("rating", 1F);
    }

    @Test
    void getAlcoholWithFields() {
        Map<String, Object> fieldsValue = new HashMap<>();

        try {
            Mockito.when(ApplicationRepositoryService.getByID(1L)).thenReturn(alcohol);
        } catch (NoEntityException e) {
            fail("Test failed! Cannot get alcohol by id");
        }

        try {
            fieldsValue = mainControllerService.getAlcoholWithFields(1L, fieldsList);
        } catch (NoEntityException e) {
            fail("Test failed! wrong entity id");
        } catch (IllegalFieldException e) {
            fail("Test failed! wrong fields request: " + fieldsList, e);
        }

        assertEquals(expectedFullMap, fieldsValue);
    }

    @Test
    void getAlcoholWithWrongFields() {
        Map<String, Object> fieldsValue = new HashMap<>();

        try {
            Mockito.when(ApplicationRepositoryService.getByID(1L)).thenReturn(alcohol);
        } catch (NoEntityException e) {
            fail("Test failed! Cannot get alcohol by id");
        }

        try {
            fieldsValue = mainControllerService.getAlcoholWithFields(1L, wrongFieldsList);
        } catch (NoEntityException e) {
            fail("Test failed! wrong entity id");
        } catch (IllegalFieldException e) {
            assertEquals(IllegalFieldException.class, e.getClass());
        }
        assertEquals(0, fieldsValue.size());
    }

    @Test
    void getAlcoholWithNoFields() {
        try {
            Mockito.when(ApplicationRepositoryService.getByID(1L)).thenReturn(alcohol);
        } catch (NoEntityException e) {
            fail("Test failed! Cannot get alcohol by id");
        }

        try {
            mainControllerService.getAlcoholWithFields(1L, emptyFieldsList);
        } catch (NoEntityException e) {
            fail("Test failed! wrong entity id");
        } catch (IllegalFieldException e) {
            assertEquals("The server reported: Alcohol entity has no field called .",
                    e.getMessage());
        }
    }
}