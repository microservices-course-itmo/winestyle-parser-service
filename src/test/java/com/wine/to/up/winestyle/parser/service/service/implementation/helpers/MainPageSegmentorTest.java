package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class MainPageSegmentorTest {
    static private MainPageSegmentor segmentor;
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
        Constructor segmentorConstructor = MainPageSegmentor.class.getDeclaredConstructor();
        segmentorConstructor.setAccessible(true);
        segmentor = (MainPageSegmentor) segmentorConstructor.newInstance();
        document = Jsoup.parse(MAIN_CONTENT_HTML);
        ReflectionTestUtils.setField(segmentor, "mainPageElementCssQuery", ".main-content");
        ReflectionTestUtils.setField(segmentor, "productElementClassName", "item-block");
    }

    @Test
    void extractProductElements() {
        assertEquals(document.selectFirst(".main-content").getElementsByClass("item-block"),
                segmentor.extractProductElements(document));
    }
}