package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.google.common.collect.ImmutableMap;
import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class ParsingControllerServiceTest {
    private ParsingControllerService parsingControllerService;
    private WinestyleParserService alcoholParserService = mock(WinestyleParserService.class);
    private StatusService statusService = mock(StatusService.class);

    @BeforeEach
    void setUp() {
        parsingControllerService = new ParsingControllerService(alcoholParserService, statusService);
    }

    @Test
    void startParsingJob() throws InterruptedException {
//        Mockito.doNothing().when(alcoholParserService).parseBuildSave("/wine/available_ll/");
//        Mockito.doNothing().when(alcoholParserService).setAlcoholType(AlcoholType.WINE);
//        Mockito.doNothing().when(alcoholParserService).setMainPageUrl("https://spb.winestyle.ru");
//        Mockito.when(statusService.tryBusy(ServiceType.PARSER)).thenReturn(true);
//        Mockito.doNothing().when(statusService).release(ServiceType.PARSER);
//        try {
//            parsingControllerService.startParsingJob(City.SPB, AlcoholType.WINE);
////            //TODO: until(startParsingJob)
//            Awaitility.await().pollDelay(Durations.ONE_SECOND).until(() -> true);
////            parsingControllerService.startParsingJob(City.SPB, AlcoholType.WINE);
//        } catch (ServiceIsBusyException e) {
//            fail("Busy!!!!!!!!!");
//        }
//        Mockito.verify(statusService, Mockito.times(1)).release(ServiceType.PARSER);
    }
}