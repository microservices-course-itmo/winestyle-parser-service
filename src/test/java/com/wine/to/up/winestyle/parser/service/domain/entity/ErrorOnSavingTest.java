package com.wine.to.up.winestyle.parser.service.domain.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorOnSavingTest {
    static Alcohol alcohol;
    static Timestamp timestamp;
    static String error;
    String expectedErrorOnSavingString = "ErrorOnSaving(id=1, " +
            "type=test, name=test, url=test, imageUrl=test, cropYear=1990, " +
            "manufacturer=test, brand=test, color=test, country=test, region=test, " +
            "volume=1.0, strength=1.0, sugar=test, price=1.0, grape=test, " +
            "taste=test, aroma=test, foodPairing=test, description=test, rating=1.0, " +
            "unsavedId=1, timestamp=1970-01-01 03:00:00.0, error=test error string)";
    String expectedErrorOnSavingNullString = "ErrorOnSaving(id=null, " +
            "type=null, name=null, url=null, imageUrl=null, cropYear=null, " +
            "manufacturer=null, brand=null, color=null, country=null, region=null, " +
            "volume=null, strength=null, sugar=null, price=null, grape=null, " +
            "taste=null, aroma=null, foodPairing=null, description=null, rating=null, " +
            "unsavedId=null, timestamp=null, error=null)";

    String expectedErrorOnSavingStringOf = "ErrorOnSaving(id=null, " +
            "type=test, name=test, url=test, imageUrl=test, cropYear=1990, " +
            "manufacturer=test, brand=test, color=test, country=test, region=test, " +
            "volume=1.0, strength=1.0, sugar=test, price=1.0, grape=test, " +
            "taste=test, aroma=test, foodPairing=test, description=test, rating=1.0, " +
            "unsavedId=1, timestamp=1970-01-01 03:00:00.0, error=test error string)";

    @BeforeAll
    static void setUp() {
        error = "test error string";
        timestamp = new Timestamp(0);
        alcohol = Alcohol.builder()
                .id(1L).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color("test").country("test").region("test")
                .volume(1F).strength(1F).sugar("test").price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();
    }

    @Test
    void of() {
        ErrorOnSaving errorOnSaving = ErrorOnSaving.of(alcohol, timestamp, error);
        String toStringValues = errorOnSaving.toString();
        assertEquals(expectedErrorOnSavingStringOf, toStringValues);
    }

    @Test
    void construct() {
        ErrorOnSaving errorOnSavingNull = new ErrorOnSaving(1L, "test", "test",
                "test", "test", "1990", "test",
                "test", "test", "test", "test",
                "1.0", 1F, "test", "1.0", "test",
                "test", "test", "test",
                "test", "1.0", 1L, timestamp, "test error string");
        String stringValues = errorOnSavingNull.toString();
        assertEquals(expectedErrorOnSavingString, stringValues);
    }

    @Test
    void constructNull() {
        ErrorOnSaving errorOnSavingNull = new ErrorOnSaving();
        String stringNullValues = errorOnSavingNull.toString();
        assertEquals(expectedErrorOnSavingNullString, stringNullValues);
    }
}