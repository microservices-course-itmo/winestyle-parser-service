package com.wine.to.up.winestyle.parser.service.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiErrorTest {

    static ApiError apiError;
    static String expectedString = "[test, fail]";

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

    @Test
    void setErrors() {
        List<String> errors = new ArrayList<>();
        errors.add("test");
        errors.add("fail");
        apiError.setErrors(errors);
        assertEquals(errors, apiError.getErrors());
    }

    @Test
    void constructApiErrors() {
        List<String> errors = new ArrayList<>();
        errors.add("test");
        errors.add("fail");
        ApiError apiError = new ApiError(errors);
        assertEquals(errors, apiError.getErrors());
    }
}