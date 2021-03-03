package com.wine.to.up.winestyle.parser.service.controller.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileCreationExeptionTest {

    String expectedMessage = "Cannot write database to file!";

    @Test
    void getMessage() {
        assertEquals(expectedMessage, new FileCreationExeption().getMessage());
    }
}