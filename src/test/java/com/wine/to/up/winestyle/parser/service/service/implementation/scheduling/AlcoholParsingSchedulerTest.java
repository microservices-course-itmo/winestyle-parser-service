package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.MainControllerService;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class AlcoholParsingSchedulerTest {
    @InjectMocks
    private AlcoholParsingScheduler alcoholParsingScheduler;
    @Mock
    private ParsingControllerService parsingControllerService;
    @Mock
    private MainControllerService mainControllerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void onScheduleParseWine() {
        try {
            Mockito.doNothing().when(parsingControllerService).startParsingJob(City.SPB, AlcoholType.WINE);
            alcoholParsingScheduler.onScheduleParseWine();
            Mockito.verify(parsingControllerService, Mockito.times(1))
                    .startParsingJob(City.SPB, AlcoholType.WINE);
        } catch (InterruptedException | ServiceIsBusyException e) {
            fail("Test failed!", e);
        }
    }

    @Test
    void onScheduleParseSparkling() {
        try {
            Mockito.doNothing().when(parsingControllerService).startParsingJob(City.SPB, AlcoholType.SPARKLING);
            alcoholParsingScheduler.onScheduleParseSparkling();
            Mockito.verify(parsingControllerService, Mockito.times(1))
                    .startParsingJob(City.SPB, AlcoholType.SPARKLING);
        } catch (InterruptedException | ServiceIsBusyException e) {
            fail("Test failed!", e);
        }
    }

    @Test
    void onScheduleLoadProxies() {
        try {
            Mockito.doNothing().when(mainControllerService).startProxiesInitJob(1);
            alcoholParsingScheduler.onScheduleLoadProxies();
        } catch (ServiceIsBusyException e) {
            fail("Test failed!", e);
        }
    }
}