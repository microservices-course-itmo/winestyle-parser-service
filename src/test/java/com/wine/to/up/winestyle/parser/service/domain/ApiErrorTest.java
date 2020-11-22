package com.wine.to.up.winestyle.parser.service.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    static ApiError apiError;
    static String expectedString = "[test fail]";

    @BeforeAll
    static void setUp() {
        List<String> errors = new ArrayList<>();
        errors.add("test fail");
        apiError = new ApiError(errors);
    }

    @Test
    void getErrors() {
        String s = apiError.getErrors().toString();
        assertEquals(expectedString, s);
    }
}