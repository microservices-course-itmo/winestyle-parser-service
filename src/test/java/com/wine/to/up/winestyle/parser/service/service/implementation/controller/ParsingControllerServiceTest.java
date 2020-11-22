package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ParsingControllerServiceTest {

    private StatusService statusService;

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
}