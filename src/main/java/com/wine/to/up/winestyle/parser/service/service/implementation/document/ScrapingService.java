package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

/**
 * Класс, который парсит страницы сайта. Скачивает html и возвращает Document.
 */
@Service
@Slf4j
public class ScrapingService {
    public Document getJsoupDocument(String url) {
        Document doc = null;
        while (doc == null) {
            String html = SeleniumWebDriver.GetResponse(url);
            doc = Jsoup.parse(html);
        }
        return doc;
    }
}
