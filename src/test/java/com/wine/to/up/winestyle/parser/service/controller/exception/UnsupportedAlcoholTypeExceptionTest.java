package com.wine.to.up.winestyle.parser.service.controller.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnsupportedAlcoholTypeExceptionTest {

    String expectedMessage = "The server reported: Bear alcohol type test exception";

    @Test
    void getMessage() {
        assertEquals(expectedMessage,
                UnsupportedAlcoholTypeException
                        .createWith("test exception", "Bear")
                        .getMessage());
    }
}