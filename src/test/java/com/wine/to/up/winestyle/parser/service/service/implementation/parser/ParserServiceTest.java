package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ApplicationContextLocator;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import com.wine.to.up.winestyle.parser.service.service.implementation.parser.job.MainJob;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
class ParserServiceTest {
    @InjectMocks
    private ParserService parserService;
    @Mock
    private Scraper scraper;
    @Mock
    private MainJob mainJob;

    String htmlPage = "div id=\"CatalogPagingBottom\"" +
            "<li>2</li>" +
            "</div>";
    String mskUrl = "https://winestyle.ru";
    String spbUrl = "https://spb.winestyle.ru";
    String wineUrl = "/wine/all/";
    String sparklingUrl = "/champagnes-and-sparkling/champagnes/sparkling/sparkling-blue_ll";
    ImmutableMap<City, String> supportedCityUrls = ImmutableMap .<City, String>builder()
                .put(City.MSK, mskUrl)
                .put(City.SPB, spbUrl)
                .build();
    ImmutableMap<AlcoholType, String> supportedAlcoholUrls = ImmutableMap.<AlcoholType, String>builder()
                .put(AlcoholType.WINE, wineUrl)
                .put(AlcoholType.SPARKLING, sparklingUrl)
                .build();

    @BeforeEach
    void setUp() throws InterruptedException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(parserService, "paginationElementCssQuery", "#CatalogPagingBottom li:last-of-type");
        ReflectionTestUtils.setField(parserService, "mskUrl", mskUrl);
        ReflectionTestUtils.setField(parserService, "spbUrl",  spbUrl);
        ReflectionTestUtils.setField(parserService, "wineUrl", wineUrl);
        ReflectionTestUtils.setField(parserService, "sparklingUrl", sparklingUrl);
        ReflectionTestUtils.setField(parserService, "supportedCityUrls", supportedCityUrls);
        ReflectionTestUtils.setField(parserService, "supportedAlcoholUrls", supportedAlcoholUrls);
        Mockito.when(mainJob.get(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(1);

        Document document = Jsoup.parse(htmlPage);
        Mockito.when(scraper.getJsoupDocument(Mockito.anyString())).thenReturn(document);
    }

    @Test
    void parseBuildSaveSpbWine() throws InterruptedException {
        parserService.parseBuildSave(AlcoholType.WINE, City.SPB);
        Mockito.verify(scraper, times(1)).getJsoupDocument(spbUrl + wineUrl);
        Mockito.verify(mainJob, times(1)).setParsed(0);
    }
    @Test
    void parseBuildSaveMskSparkling() throws InterruptedException {
        parserService.parseBuildSave(AlcoholType.SPARKLING, City.MSK);
        Mockito.verify(scraper, times(1)).getJsoupDocument(mskUrl + sparklingUrl);
        Mockito.verify(mainJob, times(1)).setParsed(0);
    }
}