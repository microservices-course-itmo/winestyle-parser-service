package com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlcoholTypeTest {

    @Test
    void toStringTest() {
        assertEquals("wine", AlcoholType.WINE.toString());
        assertEquals("sparkling", AlcoholType.SPARKLING.toString());
    }

    @Test
    void values() {
        assertTrue(AlcoholType.values().length == 2);
    }
}