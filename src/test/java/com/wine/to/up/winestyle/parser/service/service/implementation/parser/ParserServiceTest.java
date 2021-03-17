package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ApplicationContextLocator;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
class ParserServiceTest {
    @InjectMocks
    private ParserService parserService;
    @Mock
    private KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private Scraper scraper;
    @MockBean
    private ApplicationContextLocator applicationContextLocator = mock(ApplicationContextLocator.class);

    String htmlPage = "div id=\"CatalogPagingBottom\"" +
            "<li>2</li>" +
            "</div>";

    @BeforeEach
    void setUp() throws InterruptedException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(parserService, "timeout", 10);
        ReflectionTestUtils.setField(parserService, "paginationElementCssQuery", "#CatalogPagingBottom li:last-of-type");

        Document document = Jsoup.parse(htmlPage);
        Mockito.when(scraper.getJsoupDocument("test/test")).thenReturn(document);
        Mockito.when(scraper.getJsoupDocument("test/test?page=2")).thenReturn(document);
    }

    @Test
    void parseBuildSave() throws InterruptedException {
        parserService.setAlcoholType(AlcoholType.WINE);
        parserService.setMainPageUrl("test");
        try {
            parserService.parseBuildSave("/test");
        } catch (NullPointerException ex) {
            log.warn("Cannot execute MainJob inner class. Ok.");
        }
        Mockito.verify(scraper, times(1)).getJsoupDocument("test/test");
    }

    @Test
    void setAlcoholType() {
        parserService.setAlcoholType(AlcoholType.SPARKLING);
        assertEquals(AlcoholType.SPARKLING, ReflectionTestUtils.getField(parserService, "alcoholType"));
    }

    @Test
    void setMainPageUrl() {
        parserService.setMainPageUrl("test");
        assertEquals("test", ReflectionTestUtils.getField(parserService, "mainPageUrl"));
    }
}