package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

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

class ParsingControllerServiceTest {
    @InjectMocks
    ParsingControllerService parsingControllerService;
    @Mock
    WinestyleParserService alcoholParserService;
    @Spy
    StatusService statusService = new StatusService();
    @Mock
    RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void startParsingJob() throws InterruptedException, ServiceIsBusyException {
        Mockito.when(statusService.tryBusy(ServiceType.PARSER)).thenReturn(true);
        Mockito.doNothing().when(alcoholParserService).parseBuildSave(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(repositoryService).add((Timing) Mockito.any());
        parsingControllerService.startParsingJob(City.SPB, AlcoholType.WINE);
        Thread.sleep(100);
        Mockito.verify(statusService, Mockito.times(1)).release(ServiceType.PARSER);
    }
}