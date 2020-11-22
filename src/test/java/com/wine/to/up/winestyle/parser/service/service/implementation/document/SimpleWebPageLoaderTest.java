package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class SimpleWebPageLoaderTest {

    String url = "https://spb.winestyle.ru/wine/wines_ll/";
    String actualTitle = "Вино";

    @Test
    void getDocument() {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/85.0.4183.121 "
                    + "Safari/537.36")
                    .get();
            String title = doc.getElementsByClass("main-title-container").first().select("h1").text();
            assertEquals(title, actualTitle);
        } catch (IOException e) {
            fail("Can't get connection to page");
        }

    }
}