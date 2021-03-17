package com.wine.to.up.winestyle.parser.service.service.implementation.scheduling;

import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuxiliarySchedulerTest {
    @InjectMocks
    AuxiliaryScheduler auxiliaryScheduler;
    @Mock
    RepositoryService repositoryService;

    @Test
    void onScheduleUpdateLastSucceed() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(repositoryService.sinceLastSucceedParsing()).thenReturn(100D);
        auxiliaryScheduler.onScheduleUpdateLastSucceed();
    }
}