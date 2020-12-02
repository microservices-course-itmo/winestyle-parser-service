package com.wine.to.up.winestyle.parser.service.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WineDtoTest {

    static WineDto wineDto;

    @BeforeAll
    static void setUp() {
        wineDto = new WineDto();
        wineDto.setUrl("test");
        wineDto.setImageUrl("test");
        wineDto.setName("test");
        wineDto.setCropYear(1);
        wineDto.setManufacturer("test");
        wineDto.setBrand("test");
        wineDto.setColor("test");
        wineDto.setCountry("test");
        wineDto.setRegion("test");
        wineDto.setVolume(1F);
        wineDto.setStrength("test");
        wineDto.setSugar("test");
        wineDto.setPrice(1F);
        wineDto.setGrape("test");
        wineDto.setTastingNotes("test");
        wineDto.setRating(1.0);
        wineDto.setDescription("test");
    }

    @Test
    void getUrl(){
        assertEquals("test", wineDto.getUrl());
    }
    @Test
    void getImageUrl(){
        assertEquals("test", wineDto.getImageUrl());
    }
    @Test
    void getName(){
        assertEquals("test", wineDto.getName());
    }
    @Test
    void getCropYear(){
        assertEquals(1, wineDto.getCropYear());
    }
    @Test
    void getManufacturer(){
        assertEquals("test", wineDto.getManufacturer());
    }
    @Test
    void getBrand(){
        assertEquals("test", wineDto.getBrand());
    }
    @Test
    void getColor(){
        assertEquals("test", wineDto.getColor());
    }
    @Test
    void getCountry(){
        assertEquals("test", wineDto.getCountry());
    }
    @Test
    void getRegion(){
        assertEquals("test", wineDto.getRegion());
    }
    @Test
    void getVolume(){
        assertEquals(1F, wineDto.getVolume());
    }
    @Test
    void getStrength(){
        assertEquals("test", wineDto.getStrength());
    }
    @Test
    void getSugar(){
        assertEquals("test", wineDto.getSugar());
    }
    @Test
    void getPrice(){
        assertEquals(1F, wineDto.getPrice());
    }
    @Test
    void getGrape(){
        assertEquals("test", wineDto.getGrape());
    }
    @Test
    void getTastingNotes(){
        assertEquals("test", wineDto.getTastingNotes());
    }
    @Test
    void getRating(){
        assertEquals(1.0, wineDto.getRating());
    }
    @Test
    void getDescription(){
        assertEquals("test", wineDto.getDescription());
    }

}