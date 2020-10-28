package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

class ParsingControllerServiceUnitTest {
    private final StatusService statusService = spy(StatusService.class);

    private final ImmutableMap<String, String> SUPPORTED_ALCOHOL_URLS = ImmutableMap.<String, String>builder()
            .put("wine", "/wine/all/")
            .put("sparkling", "/champagnes-and-sparkling/champagnes/sparkling/sparkling-blue_ll/")
            .build();

    //checking statusService
    @Test
    void startParsingJobStatusTest() {
        boolean isBusyTrue = statusService.isBusy("wine");
        assertTrue(isBusyTrue);
    }

    @Test
    void startParsingJobChangingStatusTest() {
        assertTrue(statusService.isBusy("wine"));
        statusService.busy("wine");
        assertFalse(statusService.isBusy("wine"));
        statusService.busy("wine");
        assertTrue(statusService.isBusy("wine"));
    }


}