package com.wine.to.up.winestyle.parser.service.service.implementation.parser.job;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductBlockSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductPageSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProductJobTest {
    @InjectMocks
    private ProductJob productJob;
    @Mock
    private Director director;
    @Mock
    private KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    @Mock
    private RepositoryService repositoryService;
    @Mock
    private Scraper scraper;
    @Mock
    private ProductPageSegmentor productPageSegmentor;
    @Mock private
    ProductBlockSegmentor productBlockSegmentor;

    Alcohol expectedAlcohol = Alcohol.builder()
            .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
            .manufacturer("test").brand("test").color("test").country("test").region("test")
            .volume(1F).strength(1F).sugar("test").price(1F).city(City.SPB)
            .grape("test").taste("test").aroma("test").foodPairing("test")
            .description("test").rating(1F)
            .build();
    String mainPageUrl = "http://winestyle.ru";
    String productUrl = "/productPageUrl";
    AlcoholType alcoholType;
    Element stubElement;
    Parser parser = mock(Parser.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ParserApi.Wine.Builder kafkaMessageBuilder = mock(ParserApi.Wine.Builder.class);
        Mockito.when(director.fillKafkaMessageBuilder(expectedAlcohol, AlcoholType.WINE)).thenReturn(kafkaMessageBuilder);
        Mockito.when(director.getKafkaMessageBuilder()).thenReturn(kafkaMessageBuilder);
        Mockito.when(director.makeAlcohol(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(expectedAlcohol);
        Mockito.doNothing().when(repositoryService).add((Alcohol) Mockito.any());
        Mockito.doNothing().when(kafkaMessageSender).sendMessage(Mockito.any());
        try {
            Document document = Jsoup.parse("<div>stubbed document</div>");
            Mockito.when(scraper.getJsoupDocument(Mockito.anyString())).thenReturn(document);
        } catch (InterruptedException e) {
            fail("Interrupted by unexpected time");
        }
        try {
            Mockito.when(repositoryService.getByUrl(Mockito.anyString())).thenReturn(expectedAlcohol);
        } catch (NoEntityException e) {
            fail("Cannot stub");
        }

        Mockito.when(parser.parseAvailability()).thenReturn(Optional.of("Available"));
        Mockito.when(parser.parsePrice()).thenReturn(Optional.of(100F));
        Mockito.when(parser.parseWinestyleRating()).thenReturn(Optional.of(10F));

        Mockito.when(productPageSegmentor.extractProductPageMainContent(Mockito.any())).thenReturn(stubElement);
        Mockito.when(productPageSegmentor.extractArticlesBlock(Mockito.any())).thenReturn(stubElement);
        Mockito.when(productPageSegmentor.extractDescriptionBlock(Mockito.any())).thenReturn(stubElement);
        Mockito.when(productPageSegmentor.extractLeftBlock(Mockito.any())).thenReturn(stubElement);

        Mockito.when(productBlockSegmentor.extractInfoContainer(Mockito.any())).thenReturn(stubElement);
        Mockito.when(productBlockSegmentor.extractListDescription(Mockito.any())).thenReturn(stubElement);

        alcoholType = AlcoholType.WINE;
        stubElement = Jsoup.parse("<div>Product Element</div>").getElementsByTag("<div>").first();
    }

    @Test
    void getParsedAlcohol() {
        Alcohol actualAlcohol = productJob.getParsedAlcohol(parser, mainPageUrl, productUrl, stubElement, alcoholType, City.SPB);
        assertEquals(expectedAlcohol, actualAlcohol);
        assertEquals(mainPageUrl, ReflectionTestUtils.getField(productJob, "mainPageUrl"));
        assertEquals(productUrl, ReflectionTestUtils.getField(productJob, "productUrl"));
        assertEquals(alcoholType, ReflectionTestUtils.getField(productJob, "alcoholType"));
        assertEquals(parser, ReflectionTestUtils.getField(productJob, "parser"));
    }

    @Test
    void parseProductTest() {
        productJob.getParsedAlcohol(parser, mainPageUrl, productUrl, stubElement, alcoholType, City.SPB);
        ParserApi.WineParsedEvent.Builder kafkaMessageBuilder = mock(ParserApi.WineParsedEvent.Builder.class);
        Mockito.when(kafkaMessageBuilder.addWines((ParserApi.Wine.Builder) Mockito.any())).thenReturn(kafkaMessageBuilder);
        Alcohol actualAlcohol = ReflectionTestUtils
                .invokeMethod(productJob, "parseProduct", stubElement, kafkaMessageBuilder, City.SPB);
        assertEquals(expectedAlcohol, actualAlcohol);
    }
}