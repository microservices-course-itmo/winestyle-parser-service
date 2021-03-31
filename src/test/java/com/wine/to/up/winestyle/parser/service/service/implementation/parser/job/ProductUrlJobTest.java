package com.wine.to.up.winestyle.parser.service.service.implementation.parser.job;

import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductBlockSegmentor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class ProductUrlJobTest {
    @InjectMocks
    ProductUrlJob productUrlJob;
    @Mock
    ProductBlockSegmentor productBlockSegmentor;
    Parser parser = mock(Parser.class);
    Element element = Jsoup.parse("<div>stub element</div>");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(parser.parseUrl()).thenReturn("https://testStub");
    }

    @Test
    void get() {
        String actualUrl = productUrlJob.get(parser, element);
        assertEquals("https://testStub", actualUrl);
    }
}