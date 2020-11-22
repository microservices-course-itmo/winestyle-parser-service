package com.wine.to.up.winestyle.parser.service.controller.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientConnectionProblemTest {

    String exceptedMessage = "Error while feeding file to outputStream";

    @Test
    void getMessage() {
        assertEquals(exceptedMessage, new ClientConnectionProblem().getMessage());
    }
}