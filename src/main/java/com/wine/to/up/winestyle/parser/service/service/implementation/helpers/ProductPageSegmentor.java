package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ProductPageSegmentor {
    @Value("${spring.jsoup.segmenting.css.query.product-page}")
    private String productPageElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.description-block}")
    private String descriptionBlockElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.left-block}")
    private String leftBlockElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.articles-block}")
    private String articlesBlockElementCssQuery;

    private ProductPageSegmentor(){
    }

    public Element extractProductPageMainContent(Document doc) {
        return doc.selectFirst(productPageElementCssQuery);
    }

    public Element extractLeftBlock(Element productPageMainContent) {
        return productPageMainContent.selectFirst(leftBlockElementCssQuery);
    }

    public Element extractArticlesBlock(Element productPageMainContent) {
        return productPageMainContent.selectFirst(articlesBlockElementCssQuery);
    }

    public Element extractDescriptionBlock(Element productPageMainContent) {
        return productPageMainContent.selectFirst(descriptionBlockElementCssQuery);
    }
}
