package com.wine.to.up.winestyle.parser.service.domain;

import com.wine.to.up.winestyle.parser.service.controller.exception.FileCreationExeption;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    static ApiError apiError;
    static String expectedString = "[The server reported: busy exception., Cannot write database to file!]";

    @BeforeAll
    static void setUp() {
        List<String> errors = new ArrayList<>();
        errors.add(ServiceIsBusyException.createWith("busy exception").getMessage());
        errors.add(new FileCreationExeption().getMessage());
        apiError = new ApiError(errors);
    }

    @Test
    void getErrors() {
        String s = apiError.getErrors().toString();
        assertEquals(expectedString, s);
    }
}