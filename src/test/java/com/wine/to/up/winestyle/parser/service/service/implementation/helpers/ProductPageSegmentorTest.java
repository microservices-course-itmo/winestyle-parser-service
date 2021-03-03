package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ProductPageSegmentorTest {
    private static ProductPageSegmentor segmentor;
    static Document document;

    static String MAIN_CONTENT_HTML = "<div class=\"main-content main-content-filters\">" +
            "<form class=\"item-block \">" +
            "<div class=\"item-block-content\">" +
            "<div class=\"info-container\">" +
            "<ul class=\"list-description\">" +
            "</ul>" +
            "</div>" +
            "</div>" +
            "</form>" +
            "<div class=\"item-content\">" +
            "<div class=\"left-aside left-aside_no-bg\">" +
            "</div>" +
            "<div class=\"right-aside\">" +
            "<div class=\"articles-container articles-col\">" +
            "</div>" +
            "<div class=\"articles-container collapsible-block desc opened-half\">" +
            "<a>test</a>" +
            "</div>" +
            "</div>" +
            "</div>" +
            "</div>";

    @BeforeAll
    static void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor segmentorConstructor = ProductPageSegmentor.class.getDeclaredConstructor();
        segmentorConstructor.setAccessible(true);
        segmentor = (ProductPageSegmentor) segmentorConstructor.newInstance();
        document = Jsoup.parse(MAIN_CONTENT_HTML);
        ReflectionTestUtils.setField(segmentor, "productPageElementCssQuery", ".main-content");
        ReflectionTestUtils.setField(segmentor, "descriptionBlockElementCssQuery", ".articles-container.desc");
        ReflectionTestUtils.setField(segmentor, "leftBlockElementCssQuery", ".left-aside");
        ReflectionTestUtils.setField(segmentor, "articlesBlockElementCssQuery", ".articles-col");
    }

    @Test
    void extractProductPageMainContent() {
        assertEquals(document.selectFirst(".main-content"),
        segmentor.extractProductPageMainContent(document));
    }

    @Test
    void extractLeftBlock() {
        assertEquals(document.selectFirst(".main-content").selectFirst(".left-aside"),
        segmentor.extractLeftBlock(segmentor.extractProductPageMainContent(document)));
    }

    @Test
    void extractArticlesBlock() {
        assertEquals(document.selectFirst(".main-content").selectFirst(".articles-col"),
        segmentor.extractArticlesBlock(segmentor.extractProductPageMainContent(document)));
    }

    @Test
    void extractDescriptionBlock() {
        assertEquals(document.selectFirst(".main-content").selectFirst(".articles-container.desc"),
        segmentor.extractDescriptionBlock(segmentor.extractProductPageMainContent(document)));
    }
}