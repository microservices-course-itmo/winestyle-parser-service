package com.wine.to.up.winestyle.parser.service.controller.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceIsBusyExceptionTest {

    String expectedMessage = "The server reported: test exception.";

    @Test
    void getMessage() {
        assertEquals(expectedMessage, ServiceIsBusyException.createWith("test exception").getMessage());
    }
}