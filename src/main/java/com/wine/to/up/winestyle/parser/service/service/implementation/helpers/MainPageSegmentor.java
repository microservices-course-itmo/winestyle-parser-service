package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Класс, отвечающий за разделение данных со страницы
 */
@Component
public final class MainPageSegmentor {
    @Value("${spring.jsoup.segmenting.css.query.main-page}")
    private String mainPageElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.class.product}")
    private String productElementClassName;

    private MainPageSegmentor(){
    }

    public Elements extractProductElements(Document doc) {
        return doc.selectFirst(mainPageElementCssQuery).getElementsByClass(productElementClassName);
    }
}
