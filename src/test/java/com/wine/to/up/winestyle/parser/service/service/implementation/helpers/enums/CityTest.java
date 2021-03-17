package com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    @Test
    void testToString() {
        assertEquals(City.SPB.toString(), "spb");
        assertEquals(City.MSK.toString(), "msk");
        try{
            City.valueOf("default");
        } catch (IllegalArgumentException ex) {
            assertEquals(IllegalArgumentException.class, ex.getClass());
        }
    }
}