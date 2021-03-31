package com.wine.to.up.winestyle.parser.service.service.implementation.parser.job;

import com.wine.to.up.commonlib.logging.EventLogger;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.MainPageSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MainJobTest {
    @InjectMocks
    MainJob mainJob;
    @Mock
    private Parser parser;
    @Mock
    private ProductJob productJob;
    @Mock
    private ProductUrlJob productUrlJob;
    @Mock
    private MainPageSegmentor mainPageSegmentor;
    EventLogger eventLogger = mock(EventLogger.class);
    Document document = Jsoup.parse("<div>doc stub</div>");
    Elements stubElements = Jsoup.parse("<div>stub element</div><div>ele</div>")
            .getElementsByTag("<div>");
    Alcohol stubAlcohol = Alcohol.builder().build();
    String stubAlcoholUrl = "/alcoholUrlTest";
    String mainPageUrl = "https://winestyle.ru";


    @BeforeEach
    void startUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(mainJob, "timeout", 0);
        Mockito.when(mainPageSegmentor.extractProductElements(Mockito.any())).thenReturn(stubElements);
        Mockito.when(productJob.getParsedAlcohol(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(stubAlcohol);
        Mockito.when(productUrlJob.get(Mockito.any(), Mockito.any())).thenReturn(stubAlcoholUrl);
        ReflectionTestUtils.setField(mainJob, "eventLogger", eventLogger);
        Mockito.doNothing().when(eventLogger).info(Mockito.any(), Mockito.any());
    }

    @Test
    void get() {
        mainJob.setParsed(0);
        int unparsedActual = mainJob.get(document, AlcoholType.WINE, mainPageUrl, LocalDateTime.now(), City.SPB);
        assertEquals(0, unparsedActual);
    }

    @Test
    void setParsed() {
        mainJob.setParsed(100);
        assertEquals(100, ReflectionTestUtils.getField(mainJob, "parsed"));
    }
}