package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class SegmentorTest {
    private Segmentor segmentor;
    Document document;

    String MAIN_CONTENT_HTML = "<div class=\"main-content main-content-filters\">" +
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


    @BeforeEach
    void setUp() {
        segmentor = new Segmentor();
        document = Jsoup.parse(MAIN_CONTENT_HTML);
        ReflectionTestUtils.setField(segmentor, "mainMainElementCssQuery", ".main-content");
        ReflectionTestUtils.setField(segmentor, "productMainElementCssQuery", ".main-content");
        ReflectionTestUtils.setField(segmentor, "infoElementCssQuery", ".info-container");
        ReflectionTestUtils.setField(segmentor, "listDescriptionElementCssQuery", ".list-description");
        ReflectionTestUtils.setField(segmentor, "blockDescriptionElementCssQuery", ".articles-container.desc");
        ReflectionTestUtils.setField(segmentor, "leftBlockElementCssQuery", ".left-aside");
        ReflectionTestUtils.setField(segmentor, "articlesBlockElementCssQuery", ".articles-col");
        ReflectionTestUtils.setField(segmentor, "productElementClassName", "item-block");

        segmentor.setMainDocument(document);
        segmentor.setProductDocument(document);
        segmentor.setProductBlock(document.getAllElements().first());
    }

    @Test
    void setProductMainContent() {
        segmentor.setProductMainContent();
        assertEquals(document.selectFirst(".main-content"),
                ReflectionTestUtils.getField(segmentor, "productMainContent"));
    }

    @Test
    void breakDocumentIntoProductElements() {
        assertEquals(document.selectFirst(".main-content").getElementsByClass("item-block"),
                segmentor.breakDocumentIntoProductElements());
    }

    @Test
    void getInfoContainer() {
        assertEquals(document.selectFirst(".info-container"),
                segmentor.getInfoContainer());
    }

    @Test
    void getListDescription() {
        segmentor.setProductMainContent();
        segmentor.getInfoContainer();
        assertEquals(document.selectFirst(".list-description"),
                segmentor.getListDescription());
    }

    @Test
    void getLeftBlock() {
        segmentor.setProductMainContent();
        assertEquals(document.selectFirst(".left-aside"),
                segmentor.getLeftBlock());
    }

    @Test
    void getArticlesBlock() {
        segmentor.setProductMainContent();
        assertEquals(document.selectFirst(".articles-col"),
                segmentor.getArticlesBlock());
    }

    @Test
    void getDescriptionBlock() {
        segmentor.setProductMainContent();
        assertEquals(document.selectFirst(".articles-container.desc"),
                segmentor.getDescriptionBlock());
    }

    @Test
    void getProductBlock() {
        assertEquals(document.getAllElements().first(),
                segmentor.getProductBlock());
    }

    @Test
    void setMainDocument() {
        Document doc = Jsoup.parse("<html>testDocument</html>");
        segmentor.setMainDocument(doc);
        assertEquals(doc, ReflectionTestUtils.getField(segmentor, "mainDocument"));
    }

    @Test
    void setProductDocument() {
        Document doc = Jsoup.parse("<html>testDocument</html>");
        segmentor.setProductDocument(doc);
        assertEquals(doc, ReflectionTestUtils.getField(segmentor, "productDocument"));
    }

    @Test
    void setProductBlock() {
        Document doc = Jsoup.parse("<html>testDocument</html>");
        segmentor.setProductBlock(doc.getAllElements().first().getAllElements().first());
        assertEquals(doc, segmentor.getProductBlock());
    }
}