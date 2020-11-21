package com.wine.to.up.winestyle.parser.service.controller.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IllegalFieldExceptionTest {

    String exceptedMessage = "The server reported: wine entity has no field called wrong field.";

    @Test
    void getMessage() {
        assertEquals(exceptedMessage, IllegalFieldException.createWith("wine","wrong field").getMessage());
    }
}