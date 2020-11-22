package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class AlcoholRepositoryServiceTest {
    public static Alcohol testAlcohol;


    @BeforeAll
    static void prepareTestData(){
        testAlcohol = new Alcohol();
        testAlcohol.setPrice(1F);
        testAlcohol.setRating(1F);
    }

    @Test
    public void updatePrice() {
        Float price = testAlcohol.getPrice();
        assertNotNull(price);
        testAlcohol.setPrice(2F);
        assertNotEquals(price, testAlcohol.getPrice());
    }

    @Test
    public void updateRating() {
        Float rating = testAlcohol.getRating();
        assertNotNull(rating);
        testAlcohol.setRating(2F);
        assertNotEquals(rating, testAlcohol.getRating());
    }
}