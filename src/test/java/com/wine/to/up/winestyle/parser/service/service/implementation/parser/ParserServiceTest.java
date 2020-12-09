package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ParserServiceTest {
    @InjectMocks
    private ParserService parserService;
    @Mock
    private Scraper scraper;
    @Mock
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void initPools() {
        ExecutorService internalParsingThreadPool;
        ReflectionTestUtils.setField(parserService, "maxThreadCount", 2);

        parserService.initPools();
        internalParsingThreadPool = (ExecutorService) ReflectionTestUtils.getField(parserService, "parsingThreadPool");
        assertNotNull(internalParsingThreadPool);
        assertFalse(internalParsingThreadPool.isShutdown());

        internalParsingThreadPool.shutdown();
    }

    @Test
    void renewPool() {
        ExecutorService internalParsingThreadPool;
        ReflectionTestUtils.setField(parserService, "maxThreadCount", 2);

        parserService.initPools();
        internalParsingThreadPool = (ExecutorService) ReflectionTestUtils.getField(parserService, "parsingThreadPool");
        assertNotNull(internalParsingThreadPool);
        assertFalse(internalParsingThreadPool.isShutdown());

        internalParsingThreadPool.shutdown();
        assertTrue(internalParsingThreadPool.isShutdown());

        parserService.renewPool();
        internalParsingThreadPool = (ExecutorService) ReflectionTestUtils
                .getField(parserService, "parsingThreadPool");
        assertNotNull(internalParsingThreadPool);
        assertFalse(internalParsingThreadPool.isShutdown());

        internalParsingThreadPool.shutdown();
    }

    @Test
    void parseBuildSave() {
        ExecutorService internalParsingThreadPool;
        parserService.setMainPageUrl("test");
        String htmlPage = "div id=\"CatalogPagingBottom\"" +
                            "<li>1</li>" +
                          "</div>";
        Document document = Jsoup.parse(htmlPage);

        Future<Integer> future = mock(Future.class);
        try {
            Mockito.when(future.get()).thenReturn(1);
        } catch (InterruptedException | ExecutionException e) {
            fail("Test failed! Cannot return");
        }

        ReflectionTestUtils.setField(parserService, "parsingThreadPool", mock(ExecutorService.class));
        internalParsingThreadPool = (ExecutorService) ReflectionTestUtils.getField(parserService, "parsingThreadPool");
        Mockito.doReturn(future).when(internalParsingThreadPool).submit((Callable<Integer>) Mockito.any());


        try {
            Mockito.when(scraper.getJsoupDocument("test/test")).thenReturn(document);
            Mockito.doReturn(true).when(internalParsingThreadPool).awaitTermination(10, TimeUnit.SECONDS);
            parserService.parseBuildSave("/test");
        } catch (InterruptedException e) {
            fail("Test failed!", e);
        }

        Mockito.verify(internalParsingThreadPool, Mockito.times(1)).shutdown();
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