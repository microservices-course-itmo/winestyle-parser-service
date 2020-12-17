package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ParserDirectorTest {
    @InjectMocks
    private ParserDirector parserDirector;
    @Mock
    private ParserApi.Wine.Builder kafkaMessageBuilder;

    Parser parser = mock(Parser.class);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(parser.parseName()).thenReturn("test");
        Mockito.when(parser.parseAroma()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseBrand()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseCountry()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseCropYear()).thenReturn(java.util.Optional.of(1990));
        Mockito.when(parser.parseDescription()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseFoodPairing()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseGrape()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseImageUrl()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseManufacturer()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parsePrice()).thenReturn(java.util.Optional.of(1F));
        Mockito.when(parser.parseRegion()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseStrength()).thenReturn(java.util.Optional.of(1F));
        Mockito.when(parser.parseTaste()).thenReturn(java.util.Optional.of("test"));
        Mockito.when(parser.parseType(false)).thenReturn("test");
        Mockito.when(parser.parseUrl()).thenReturn("test");
        Mockito.when(parser.parseVolume()).thenReturn(java.util.Optional.of(1F));
        Mockito.when(parser.parseWinestyleRating()).thenReturn(java.util.Optional.of(1F));
    }

    @Test
    void makeAlcoholRedSweet() {
        String expectedColor = "Красное";
        String expectedSugar = "Сладкое";

        Alcohol expectedAlcohol = Alcohol.builder()
                .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color(expectedColor).country("test").region("test")
                .volume(1F).strength(1F).sugar(expectedSugar).price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();

        Mockito.when(parser.parseSugar()).thenReturn(java.util.Optional.of(expectedSugar));
        Mockito.when(parser.parseColor()).thenReturn(java.util.Optional.of(expectedColor));

        Alcohol alcohol = parserDirector
                .makeAlcohol(parser, "test", "test", AlcoholType.WINE);
        ReflectionTestUtils.setField(expectedAlcohol, "dateAdded", alcohol.getDateAdded());
        assertEquals(expectedColor, alcohol.getColor());
        assertEquals(expectedSugar, alcohol.getSugar());
        assertEquals(expectedAlcohol.toString(), alcohol.toString());
    }

    @Test
    void makeAlcoholOrangeMedium() {
        String expectedColor = "Оранжевое";
        String expectedSugar = "Полусладкое";

        Alcohol expectedAlcohol = Alcohol.builder()
                .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color(expectedColor).country("test").region("test")
                .volume(1F).strength(1F).sugar(expectedSugar).price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();

        Mockito.when(parser.parseSugar()).thenReturn(java.util.Optional.of(expectedSugar));
        Mockito.when(parser.parseColor()).thenReturn(java.util.Optional.of(expectedColor));

        Alcohol alcohol = parserDirector
                .makeAlcohol(parser, "test", "test", AlcoholType.WINE);
        ReflectionTestUtils.setField(expectedAlcohol, "dateAdded", alcohol.getDateAdded());
        assertEquals(expectedColor, alcohol.getColor());
        assertEquals(expectedSugar, alcohol.getSugar());
        assertEquals(expectedAlcohol.toString(), alcohol.toString());
    }

    @Test
    void makeAlcoholRoseMediumDry() {
        String expectedColor = "Розовое";
        String expectedSugar = "Полусухое";

        Alcohol expectedAlcohol = Alcohol.builder()
                .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color(expectedColor).country("test").region("test")
                .volume(1F).strength(1F).sugar(expectedSugar).price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();

        Mockito.when(parser.parseSugar()).thenReturn(java.util.Optional.of(expectedSugar));
        Mockito.when(parser.parseColor()).thenReturn(java.util.Optional.of(expectedColor));

        Alcohol alcohol = parserDirector
                .makeAlcohol(parser, "test", "test", AlcoholType.WINE);
        ReflectionTestUtils.setField(expectedAlcohol, "dateAdded", alcohol.getDateAdded());
        assertEquals(expectedColor, alcohol.getColor());
        assertEquals(expectedSugar, alcohol.getSugar());
        assertEquals(expectedAlcohol.toString(), alcohol.toString());
    }

    @Test
    void makeAlcoholBlueDry() {
        String expectedColor = "Голубое";
        String expectedSugar = "Брют";

        Alcohol expectedAlcohol = Alcohol.builder()
                .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color(expectedColor).country("test").region("test")
                .volume(1F).strength(1F).sugar(expectedSugar).price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();

        Mockito.when(parser.parseSugar()).thenReturn(java.util.Optional.of(expectedSugar));
        Mockito.when(parser.parseColor()).thenReturn(java.util.Optional.of(expectedColor));

        Alcohol alcohol = parserDirector
                .makeAlcohol(parser, "test", "test", AlcoholType.WINE);
        ReflectionTestUtils.setField(expectedAlcohol, "dateAdded", alcohol.getDateAdded());
        assertEquals(expectedColor, alcohol.getColor());
        assertEquals(expectedSugar, alcohol.getSugar());
        assertEquals(expectedAlcohol.toString(), alcohol.toString());
    }

    @Test
    void makeAlcoholWhiteDry() {
        String expectedColor = "Белое";
        String expectedSugar = "Сухое";

        Alcohol expectedAlcohol = Alcohol.builder()
                .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color(expectedColor).country("test").region("test")
                .volume(1F).strength(1F).sugar(expectedSugar).price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();

        Mockito.when(parser.parseSugar()).thenReturn(java.util.Optional.of(expectedSugar));
        Mockito.when(parser.parseColor()).thenReturn(java.util.Optional.of(expectedColor));

        Alcohol alcohol = parserDirector
                .makeAlcohol(parser, "test", "test", AlcoholType.WINE);
        ReflectionTestUtils.setField(expectedAlcohol, "dateAdded", alcohol.getDateAdded());
        assertEquals(expectedColor, alcohol.getColor());
        assertEquals(expectedSugar, alcohol.getSugar());
        assertEquals(expectedAlcohol.toString(), alcohol.toString());
    }

    @Test
    void makeAlcoholNull() {
        Mockito.when(parser.parseName()).thenReturn(String.valueOf(Optional.empty()));
        Mockito.when(parser.parseAroma()).thenReturn(Optional.empty());
        Mockito.when(parser.parseBrand()).thenReturn(Optional.empty());
        Mockito.when(parser.parseCountry()).thenReturn(Optional.empty());
        Mockito.when(parser.parseCropYear()).thenReturn(Optional.empty());
        Mockito.when(parser.parseDescription()).thenReturn(Optional.empty());
        Mockito.when(parser.parseFoodPairing()).thenReturn(Optional.empty());
        Mockito.when(parser.parseGrape()).thenReturn(Optional.empty());
        Mockito.when(parser.parseImageUrl()).thenReturn(Optional.empty());
        Mockito.when(parser.parseManufacturer()).thenReturn(Optional.empty());
        Mockito.when(parser.parsePrice()).thenReturn(Optional.empty());
        Mockito.when(parser.parseRegion()).thenReturn(Optional.empty());
        Mockito.when(parser.parseStrength()).thenReturn(Optional.empty());
        Mockito.when(parser.parseTaste()).thenReturn(Optional.empty());
        Mockito.when(parser.parseType(false)).thenReturn(String.valueOf(Optional.empty()));
        Mockito.when(parser.parseUrl()).thenReturn(String.valueOf(Optional.empty()));
        Mockito.when(parser.parseVolume()).thenReturn(Optional.empty());
        Mockito.when(parser.parseWinestyleRating()).thenReturn(Optional.empty());
        Mockito.when(parser.parseSugar()).thenReturn(Optional.empty());
        Mockito.when(parser.parseColor()).thenReturn(Optional.empty());

        Alcohol expectedAlcohol = Alcohol.builder().name(String.valueOf(Optional.empty()))
                .type(String.valueOf(Optional.empty()))
                .url("test").build();

        Alcohol alcohol = parserDirector
                .makeAlcohol(parser, "test", "test", AlcoholType.WINE);
        ReflectionTestUtils.setField(expectedAlcohol, "dateAdded", alcohol.getDateAdded());
        assertEquals(null, alcohol.getColor());
        assertEquals(null, alcohol.getSugar());
        assertEquals(expectedAlcohol.toString(), alcohol.toString());
    }

}