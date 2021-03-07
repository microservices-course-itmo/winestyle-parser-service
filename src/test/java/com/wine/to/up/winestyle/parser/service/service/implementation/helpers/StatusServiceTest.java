package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusServiceTest {
    private StatusService statusService;

    @BeforeEach
    void setUp() {
        statusService = new StatusService();
    }

    @Test
    void releaseParser() {
        statusService.tryBusy(ServiceType.PARSER);
        assertTrue(statusService.isBusy(ServiceType.PARSER));
        statusService.release(ServiceType.PARSER);
        assertFalse(statusService.isBusy(ServiceType.PARSER));
    }

    @Test
    void tryBusyParser() {
        assertFalse(statusService.isBusy(ServiceType.PARSER));
        statusService.tryBusy(ServiceType.PARSER);
        assertTrue(statusService.isBusy(ServiceType.PARSER));

    }

    @Test
    void isBusyParser() {
        statusService.tryBusy(ServiceType.PARSER);
        assertTrue(statusService.isBusy(ServiceType.PARSER));
        statusService.release(ServiceType.PARSER);
        assertFalse(statusService.isBusy(ServiceType.PARSER));
    }

    @Test
    void releaseProxy() {
        statusService.tryBusy(ServiceType.PROXY);
        assertTrue(statusService.isBusy(ServiceType.PROXY));
        statusService.release(ServiceType.PROXY);
        assertFalse(statusService.isBusy(ServiceType.PROXY));
    }

    @Test
    void tryBusyProxy() {
        assertFalse(statusService.isBusy(ServiceType.PROXY));
        statusService.tryBusy(ServiceType.PROXY);
        assertTrue(statusService.isBusy(ServiceType.PROXY));

    }

    @Test
    void isBusyProxy() {
        statusService.tryBusy(ServiceType.PROXY);
        assertTrue(statusService.isBusy(ServiceType.PROXY));
        statusService.release(ServiceType.PROXY);
        assertFalse(statusService.isBusy(ServiceType.PROXY));
    }
}