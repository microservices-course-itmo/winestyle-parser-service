package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ProductBlockSegmentorTest {
    static private ProductBlockSegmentor segmentor;
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
        Constructor segmentorConstructor = ProductBlockSegmentor.class.getDeclaredConstructor();
        segmentorConstructor.setAccessible(true);
        segmentor = (ProductBlockSegmentor) segmentorConstructor.newInstance();
        document = Jsoup.parse(MAIN_CONTENT_HTML);
        ReflectionTestUtils.setField(segmentor, "infoElementCssQuery", ".info-container");
        ReflectionTestUtils.setField(segmentor, "listDescriptionElementCssQuery", ".list-description");
    }

    @Test
    void extractInfoContainer() {
        assertEquals(document.selectFirst(".main-content").getElementsByClass("item-block").first()
                        .selectFirst(".info-container"),
        segmentor.extractInfoContainer(document.selectFirst(".main-content")
                .getElementsByClass("item-block").first())
        );
    }

    @Test
    void extractListDescription() {
        assertEquals(document.selectFirst(".main-content").getElementsByClass("item-block").first()
                        .selectFirst(".list-description"),
        segmentor.extractListDescription(document.selectFirst(".main-content").getElementsByClass("item-block").first()));
    }
}