package com.wine.to.up.winestyle.parser.service.domain.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ErrorOnSavingTest {
    static Alcohol alcohol;
    static Alcohol alcoholNull;
    static Timestamp timestamp;
    static String error;
    String expectedErrorOnSavingString = "ErrorOnSaving(id=1, " +
            "type=test, name=test, url=test, imageUrl=test, cropYear=1990, " +
            "manufacturer=test, brand=test, color=test, country=test, region=test, " +
            "volume=1.0, strength=test, sugar=test, price=1.0, grape=test, " +
            "taste=test, aroma=test, foodPairing=test, description=test, rating=1.0, " +
            "unsavedId=1, timestamp=1970-01-01 03:00:00.0, error=test error string)";
    String expectedErrorOnSavingNullString = "ErrorOnSaving(id=null, " +
            "type=null, name=null, url=null, imageUrl=null, cropYear=null, " +
            "manufacturer=null, brand=null, color=null, country=null, region=null, " +
            "volume=null, strength=null, sugar=null, price=null, grape=null, " +
            "taste=null, aroma=null, foodPairing=null, description=null, rating=null, " +
            "unsavedId=null, timestamp=1970-01-01 03:00:00.0, error=test error string)";

    @BeforeAll
    static void setUp() {
        error = "test error string";
        timestamp = new Timestamp(0);
        alcohol = Alcohol.builder()
                .id(1L).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color("test").country("test").region("test")
                .volume(1F).strength("test").sugar("test").price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();
        alcoholNull = new Alcohol();
    }

    @Test
    void of() {
        ErrorOnSaving errorOnSaving = ErrorOnSaving.builder()
                .id(alcohol.getId())
                .name(alcohol.getName())
                .type(alcohol.getType())
                .url(alcohol.getUrl())
                .imageUrl(alcohol.getImageUrl())
                .cropYear(alcohol.getCropYear() == null ? null : alcohol.getCropYear().toString())
                .manufacturer(alcohol.getManufacturer())
                .brand(alcohol.getBrand())
                .color(alcohol.getColor())
                .country(alcohol.getCountry())
                .region(alcohol.getRegion())
                .volume(alcohol.getVolume() == null ? null : alcohol.getVolume().toString())
                .strength(alcohol.getStrength())
                .sugar(alcohol.getSugar())
                .price(alcohol.getPrice() == null ? null : alcohol.getPrice().toString())
                .grape(alcohol.getGrape())
                .taste(alcohol.getTaste())
                .aroma(alcohol.getAroma())
                .foodPairing(alcohol.getFoodPairing())
                .description(alcohol.getDescription())
                .rating(alcohol.getRating() == null ? null : alcohol.getRating().toString())
                .unsavedId(alcohol.getId())
                .error(error)
                .timestamp(timestamp)
                .build();
        assertEquals(errorOnSaving.toString(), expectedErrorOnSavingString);
    }

    @Test
    void ofNull(){
        ErrorOnSaving errorOnSaving = ErrorOnSaving.builder()
                .id(alcoholNull.getId())
                .name(alcoholNull.getName())
                .type(alcoholNull.getType())
                .url(alcoholNull.getUrl())
                .imageUrl(alcoholNull.getImageUrl())
                .cropYear(alcoholNull.getCropYear() == null ? null : alcoholNull.getCropYear().toString())
                .manufacturer(alcoholNull.getManufacturer())
                .brand(alcoholNull.getBrand())
                .color(alcoholNull.getColor())
                .country(alcoholNull.getCountry())
                .region(alcoholNull.getRegion())
                .volume(alcoholNull.getVolume() == null ? null : alcoholNull.getVolume().toString())
                .strength(alcoholNull.getStrength())
                .sugar(alcoholNull.getSugar())
                .price(alcoholNull.getPrice() == null ? null : alcoholNull.getPrice().toString())
                .grape(alcoholNull.getGrape())
                .taste(alcoholNull.getTaste())
                .aroma(alcoholNull.getAroma())
                .foodPairing(alcoholNull.getFoodPairing())
                .description(alcoholNull.getDescription())
                .rating(alcoholNull.getRating() == null ? null : alcoholNull.getRating().toString())
                .unsavedId(alcoholNull.getId())
                .error(error)
                .timestamp(timestamp)
                .build();
        assertEquals(errorOnSaving.toString(), expectedErrorOnSavingNullString);
    }
}