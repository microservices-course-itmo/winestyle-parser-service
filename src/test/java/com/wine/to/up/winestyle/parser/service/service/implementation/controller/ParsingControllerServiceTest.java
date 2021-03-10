package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Timing;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class ParsingControllerServiceTest {
    @InjectMocks
    ParsingControllerService parsingControllerService;
    @Mock
    WinestyleParserService alcoholParserService;
    @Spy
    StatusService statusService = new StatusService();
    @Mock
    RepositoryService repositoryService;

    String mskUrl = "https://winestyle.ru";
    String spbUrl = "https://spb.winestyle.ru";
    String wineUrl = "/wine/available_ll";
    String sparklingUrl = "/champagnes-and-sparkling/champagnes/sparkling/sparkling-blue/available_ll/";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(parsingControllerService, "mskUrl", mskUrl);
        ReflectionTestUtils.setField(parsingControllerService, "spbUrl", spbUrl);
        ReflectionTestUtils.setField(parsingControllerService, "wineUrl", wineUrl);
        ReflectionTestUtils.setField(parsingControllerService, "sparklingUrl", sparklingUrl);
        ReflectionTestUtils.invokeMethod(parsingControllerService, "populateUrl");
    }

    @Test
    void startParsingJob() throws InterruptedException, ServiceIsBusyException {
        Mockito.when(statusService.tryBusy(ServiceType.PARSER)).thenReturn(true);
        Mockito.doNothing().when(alcoholParserService).parseBuildSave(Mockito.any());
        Mockito.doNothing().when(repositoryService).add((Timing) Mockito.any());
        parsingControllerService.startParsingJob(City.SPB, AlcoholType.WINE);
        Thread.sleep(100);
        Mockito.verify(statusService, Mockito.times(1)).release(ServiceType.PARSER);
    }

    @Test
    void populateUrl() {
        ImmutableMap<City, String> supportedCityUrls = (ImmutableMap<City, String>) ReflectionTestUtils.getField(parsingControllerService, "supportedCityUrls");
        assertEquals(supportedCityUrls.get(City.SPB), spbUrl);
    }
}