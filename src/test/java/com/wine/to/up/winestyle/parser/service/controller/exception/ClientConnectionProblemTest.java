package com.wine.to.up.winestyle.parser.service.controller.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientConnectionProblemTest {

    String expectedMessage = "Error while feeding file to outputStream";

    @Test
    void getMessage() {
        assertEquals(expectedMessage, new ClientConnectionProblem().getMessage());
    }
}