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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void onScheduleParseSpbWine() {
        try {
            Mockito.doNothing().when(parsingControllerService).startParsingJob(City.SPB, AlcoholType.WINE);
            alcoholParsingScheduler.onScheduleParseAlcoholSpbWine();
            Mockito.verify(parsingControllerService, Mockito.times(1))
                    .startParsingJob(City.SPB, AlcoholType.WINE);
        } catch (InterruptedException | ServiceIsBusyException e) {
            fail("Test failed!", e);
        }
    }

    @Test
    void onScheduleParseMskWine() {
        try {
            Mockito.doNothing().when(parsingControllerService).startParsingJob(City.MSK, AlcoholType.WINE);
            alcoholParsingScheduler.onScheduleParseAlcoholMskWine();
            Mockito.verify(parsingControllerService, Mockito.times(1))
                    .startParsingJob(City.MSK, AlcoholType.WINE);
        } catch (InterruptedException | ServiceIsBusyException e) {
            fail("Test failed!", e);
        }
    }

    @Test
    void onScheduleParseSpbSparkling() {
        try {
            Mockito.doNothing().when(parsingControllerService).startParsingJob(City.SPB, AlcoholType.SPARKLING);
            alcoholParsingScheduler.onScheduleParseAlcoholSpbSparkling();
            Mockito.verify(parsingControllerService, Mockito.times(1))
                    .startParsingJob(City.SPB, AlcoholType.SPARKLING);
        } catch (InterruptedException | ServiceIsBusyException e) {
            fail("Test failed!", e);
        }
    }

    @Test
    void onScheduleParseMskSparkling() {
        try {
            Mockito.doNothing().when(parsingControllerService).startParsingJob(City.MSK, AlcoholType.SPARKLING);
            alcoholParsingScheduler.onScheduleParseAlcoholMskSparkling();
            Mockito.verify(parsingControllerService, Mockito.times(1))
                    .startParsingJob(City.MSK, AlcoholType.SPARKLING);
        } catch (InterruptedException | ServiceIsBusyException e) {
            fail("Test failed!", e);
        }
    }
}