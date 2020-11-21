package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentationServiceTest {
    static Document doc;
    String ATTRIBUTE_MAINCONTENT = ".main-content";
    String ATTRIBUTE_ITEMBLOCK = "item-block";
    String ATTRIBUTE_INFOCONTAINER = ".info-container";
    String ATTRIBUTE_LISTDESCRIPTION = ".list-description";
    String ATTRIBUTE_LEFTASIDE = ".left-aside";
    String ATTRIBUTE_ARTICLESCOLUMN = ".articles-col";
    String ATTRIBUTE_ARTICLESCONTAINTERDESCRIPTION = ".articles-container.desc";

    @BeforeAll
    static void setUp() {
        String HTML_TEXT = "<div class=\"main-content main-content-filters\">" +
                "<form class=\"item-block \">" +
                "<div class=\"info-container\">" +
                "<ul class=\"list-description\">" +
                "<li>1</li>" +
                "</ul>" +
                "</div>" +
                "<div class=\"left-aside\">" +
                "</div>" +
                "<div class=\"articles-col\">" +
                "</div>" +
                "<div class=\"articles-container desc\">" +
                "Красное" +
                "</div>" +
                "</form>" +
                "</div>";
        doc = Jsoup.parse(HTML_TEXT);
    }

    @Test
    void setMainMainContent() {
        assertTrue(doc.selectFirst(ATTRIBUTE_MAINCONTENT).hasClass("main-content"));
    }

    @Test
    void setProductMainContent() {
        assertTrue(doc.selectFirst(ATTRIBUTE_MAINCONTENT).hasClass("main-content"));
    }

    @Test
    void breakDocumentIntoProductElements() {
        Element mainMainContent = doc.selectFirst(ATTRIBUTE_MAINCONTENT);
        Element itemBlockContent = mainMainContent.getElementsByClass(ATTRIBUTE_ITEMBLOCK).first();
        assertTrue(itemBlockContent.hasClass("item-block"));
    }

    @Test
    void getInfoContainer() {
        Element productBlock = doc.selectFirst(ATTRIBUTE_MAINCONTENT);
        Element infoContainer = productBlock.selectFirst(ATTRIBUTE_INFOCONTAINER);
        assertTrue(infoContainer.hasClass("info-container"));
    }

    @Test
    void getListDescription() {
        Element productBlock = doc.selectFirst(ATTRIBUTE_MAINCONTENT);
        Element infoContainer = productBlock.selectFirst(ATTRIBUTE_INFOCONTAINER);
        Element listDescriptionContainer = infoContainer.selectFirst(ATTRIBUTE_LISTDESCRIPTION);
        assertTrue(listDescriptionContainer.hasClass("list-description"));
        assertEquals("1", listDescriptionContainer.text());
    }

    @Test
    void getLeftBlock() {
        assertTrue(doc.selectFirst(ATTRIBUTE_MAINCONTENT)
                .selectFirst(ATTRIBUTE_LEFTASIDE)
                .hasClass("left-aside"));
    }

    @Test
    void getArticlesBlock() {
        assertTrue(doc.selectFirst(ATTRIBUTE_MAINCONTENT)
                .selectFirst(ATTRIBUTE_ARTICLESCOLUMN)
                .hasClass("articles-col"));
    }

    @Test
    void getDescriptionBlock() {
        assertTrue(doc.selectFirst(ATTRIBUTE_MAINCONTENT)
                .selectFirst(ATTRIBUTE_ARTICLESCONTAINTERDESCRIPTION)
                .hasClass("desc"));
        assertEquals("Красное", doc.selectFirst(ATTRIBUTE_MAINCONTENT)
                                        .selectFirst(ATTRIBUTE_ARTICLESCONTAINTERDESCRIPTION).text());
    }
}