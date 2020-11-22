package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ParsingControllerServiceTest {

    private StatusService statusService;

    private ImmutableMap<AlcoholType, String> SUPPORTED_ALCOHOL_URLS = ImmutableMap .<AlcoholType, String>builder()
                .put(AlcoholType.WINE, "WINE_URL")
                .put(AlcoholType.SPARKLING, "SPARKLING_URL")
                .build();

    @BeforeEach
    public void setUp(){
        statusService = new StatusService();
    }

    //checking statusService - PARSER
    @Test
    void startParsingJobParserStatusTest() {
        assertTrue(statusService.tryBusy(ServiceType.PARSER));
        assertFalse(statusService.tryBusy(ServiceType.PARSER));
        statusService.release(ServiceType.PARSER);
        assertTrue(statusService.tryBusy(ServiceType.PARSER));
    }
    //checking statusService - PARSER
    @Test
    void startParsingJobProxyStatusTest() {
        assertTrue(statusService.tryBusy(ServiceType.PROXY));
        assertFalse(statusService.tryBusy(ServiceType.PROXY));
        statusService.release(ServiceType.PROXY);
        assertTrue(statusService.tryBusy(ServiceType.PROXY));
    }

    @Test
    void getAlcoholTypeUrl() {
        assertEquals("WINE_URL", SUPPORTED_ALCOHOL_URLS.get(AlcoholType.WINE));
        assertEquals("SPARKLING_URL", SUPPORTED_ALCOHOL_URLS.get(AlcoholType.SPARKLING));
    }
}