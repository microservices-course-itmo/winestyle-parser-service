package com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTypeTest {

    @Test
    void testToString() {
        assertEquals("PARSER", ServiceType.PARSER.toString());
        assertEquals("PROXY", ServiceType.PROXY.toString());
    }

    @Test
    void values(){
        assertTrue(ServiceType.values().length == 2);
    }
}