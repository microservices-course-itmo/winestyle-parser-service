package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.MainPageSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class ParserServiceTest {
    @InjectMocks
    private ParserService parserService;
    @Mock
    private KafkaMessageSender kafkaMessageSender;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private Scraper scraper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(parserService, "maxThreadCount", 1);
        ReflectionTestUtils.setField(parserService, "daysUntilRecordsUpdate", 1);
        ReflectionTestUtils.setField(parserService, "timeout", 0);
        ReflectionTestUtils.setField(parserService, "paginationElementCssQuery", "#CatalogPagingBottom li:last-of-type");
    }

    @Test
    void parseBuildSave() {
//        parserService.setAlcoholType(AlcoholType.WINE);
//        parserService.setMainPageUrl("test");
//
//        ExecutorService internalParsingThreadPool;
//        parserService.setMainPageUrl("test");
//        String htmlPage = "div id=\"CatalogPagingBottom\"" +
//                            "<li>1</li>" +
//                          "</div>";
//        Document document = Jsoup.parse(htmlPage);
//        try {
//            Mockito.when(scraper.getJsoupDocument(Mockito.anyString())).thenReturn(document);
//        } catch (InterruptedException e) {
//            fail("Test failed! Cannot get document by url test/test");
//        }
//
//        Future<Integer> future = mock(Future.class);
//        try {
//            Mockito.when(future.get()).thenReturn(1);
//        } catch (InterruptedException | ExecutionException e) {
//            fail("Test failed! Cannot return");
//        }
//        ExecutorService executorService = mock(ExecutorService.class);
//        ReflectionTestUtils.setField(parserService, "mainPageParsingThreadPool", executorService);
//        ReflectionTestUtils.setField(parserService, "productPageParsingThreadPool", executorService);
//        ReflectionTestUtils.setField(parserService, "urlFetchingThreadPool", executorService);
//        internalParsingThreadPool = (ExecutorService) ReflectionTestUtils.getField(parserService, "mainPageParsingThreadPool");
//        Mockito.when(executorService.submit((Callable<Integer>) Mockito.any())).thenReturn(future);
//
//        try {
//            Mockito.when(scraper.getJsoupDocument("test/test")).thenReturn(document);
//            Mockito.doReturn(true).when(internalParsingThreadPool).awaitTermination(10, TimeUnit.SECONDS);
//            parserService.parseBuildSave("/test");
//        } catch (InterruptedException e) {
//            fail("Test failed!", e);
//        }
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