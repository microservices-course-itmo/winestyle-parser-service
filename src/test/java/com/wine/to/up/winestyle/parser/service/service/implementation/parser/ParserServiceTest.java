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

    @Mock
    ExecutorService mainPageParsingThreadPool;
    @Spy
    ExecutorService productPageParsingThreadPool;
    @Spy
    ExecutorService urlFetchingThreadPool;

    ThreadFactory mainPageParsingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Main_parser-%d")
            .build();
    ThreadFactory productPageParsingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Prod_parser-%d")
            .build();
    ThreadFactory urlFetchingThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Url_fetching-%d")
            .build();

    String htmlPage = "div id=\"CatalogPagingBottom\"" +
            "<li>1</li>" +
            "</div>";

    @BeforeEach
    void setUp() throws InterruptedException, ExecutionException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(parserService, "maxThreadCount", 2);
        ReflectionTestUtils.setField(parserService, "timeout", 10);
        ReflectionTestUtils.setField(parserService, "paginationElementCssQuery", "#CatalogPagingBottom li:last-of-type");

        mainPageParsingThreadPool = spy(ExecutorService.class);
        productPageParsingThreadPool = spy(Executors.newFixedThreadPool(2, productPageParsingThreadFactory));
        urlFetchingThreadPool = spy(Executors.newFixedThreadPool(2, urlFetchingThreadFactory));
        ReflectionTestUtils.setField(parserService, "mainPageParsingThreadPool", mainPageParsingThreadPool);
        ReflectionTestUtils.setField(parserService, "productPageParsingThreadPool", productPageParsingThreadPool);
        ReflectionTestUtils.setField(parserService, "urlFetchingThreadPool", urlFetchingThreadPool);

        Document document = Jsoup.parse(htmlPage);
        Mockito.when(scraper.getJsoupDocument("test/test")).thenReturn(document);
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
        Mockito.verify(mainPageParsingThreadPool, times(1)).shutdownNow();
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