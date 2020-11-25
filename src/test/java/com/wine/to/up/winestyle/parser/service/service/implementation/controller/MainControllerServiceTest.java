package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerServiceTest {

    static long id;
    static Alcohol alcohol;
    static String fieldsList;
    static String emptyFieldsList;
    static String wrongFieldsList;

    @BeforeAll
    static void setUp() {
        id = 1L;
        alcohol = new Alcohol();
        fieldsList = "id,name,type,url,imageUrl,cropYear,manufacturer,brand,color,country,region,volume" +
                ",strength,sugar,price,grape,taste,aroma,foodPairing,description,rating";
        emptyFieldsList = "";
        wrongFieldsList = "name,wrong,bad";
    }

    @Test
    void getAlcoholWithFields() {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(fieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        PropertyDescriptor pd;
        for (String fieldName : requiredFields) {
            try {
                Alcohol.class.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                fail();
            }
            try {
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                pd = new PropertyDescriptor(fieldName, Alcohol.class, getterName, null);
                res.put(fieldName, pd.getReadMethod().invoke(alcohol));
            } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                fail();
            }
        }
        assertTrue(res.containsKey("id"));
        assertTrue(res.containsKey("name"));
        assertTrue(res.containsKey("type"));
        assertTrue(res.containsKey("url"));
        assertTrue(res.containsKey("imageUrl"));
        assertTrue(res.containsKey("cropYear"));
        assertTrue(res.containsKey("manufacturer"));
        assertTrue(res.containsKey("brand"));
        assertTrue(res.containsKey("color"));
        assertTrue(res.containsKey("country"));
        assertTrue(res.containsKey("region"));
        assertTrue(res.containsKey("volume"));
        assertTrue(res.containsKey("strength"));
        assertTrue(res.containsKey("sugar"));
        assertTrue(res.containsKey("price"));
        assertTrue(res.containsKey("grape"));
        assertTrue(res.containsKey("taste"));
        assertTrue(res.containsKey("aroma"));
        assertTrue(res.containsKey("foodPairing"));
        assertTrue(res.containsKey("description"));
        assertTrue(res.containsKey("rating"));
    }

    @Test
    void getAlcoholWithOneRightTwoWrongFields() {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(wrongFieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        PropertyDescriptor pd;
        for (String fieldName : requiredFields) {
            try {
                Alcohol.class.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                assertTrue(true);
                continue;
            }
            try {
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                pd = new PropertyDescriptor(fieldName, Alcohol.class, getterName, null);
                res.put(fieldName, pd.getReadMethod().invoke(alcohol));
            } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                fail();
            }
            assertEquals("name", fieldName);
        }
        assertEquals(1, res.size());
    }

    @Test
    void getAlcoholWithNoFields() {
        Set<String> requiredFields = new HashSet<>(Arrays.asList(emptyFieldsList.split(",")));
        Map<String, Object> res = new HashMap<>();
        PropertyDescriptor pd;
        for (String fieldName : requiredFields) {
            try {
                Alcohol.class.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                continue ;
            }
            try {
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                pd = new PropertyDescriptor(fieldName, Alcohol.class, getterName, null);
                res.put(fieldName, pd.getReadMethod().invoke(alcohol));
            } catch (IllegalAccessException | IntrospectionException | InvocationTargetException e) {
                fail();
            }
        }
        assertEquals(0, res.size());
    }
}