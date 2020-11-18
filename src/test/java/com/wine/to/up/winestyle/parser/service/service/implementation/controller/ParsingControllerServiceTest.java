package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParsingControllerServiceTest {

    private StatusService statusService;

    private static final ImmutableMap<String, String> SUPPORTED_ALCOHOL_URLS = ImmutableMap.<String, String>builder()
            .put("wine", "/wine/all/")
            .put("sparkling", "/champagnes-and-sparkling/champagnes/sparkling/sparkling-blue_ll/")
            .build();

    @BeforeEach
    public void setUp(){
        statusService = new StatusService();
    }

    //checking statusService
    @Test
    void startParsingJobStatusTest() {
        assertTrue(statusService.tryBusy());
        assertFalse(statusService.tryBusy());
        statusService.release();
        assertTrue(statusService.tryBusy());
    }
}