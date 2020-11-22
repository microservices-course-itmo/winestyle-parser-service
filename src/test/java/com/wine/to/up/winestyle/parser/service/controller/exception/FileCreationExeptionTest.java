package com.wine.to.up.winestyle.parser.service.controller.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileCreationExeptionTest {

    String exceptedMessage = "Cannot write database to file!";

    @Test
    void getMessage() {
        assertEquals(exceptedMessage, new FileCreationExeption().getMessage());
    }
}