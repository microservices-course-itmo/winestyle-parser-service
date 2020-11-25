package com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlcoholTypeTest {

    @Test
    void testToString() {
        assertEquals("wine", AlcoholType.WINE.toString());
        assertEquals("sparkling", AlcoholType.SPARKLING.toString());
    }
}