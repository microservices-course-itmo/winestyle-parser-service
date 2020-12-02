package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ProductBlockSegmentor {
    @Value("${spring.jsoup.segmenting.css.query.info}")
    private String infoElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.list-description}")
    private String listDescriptionElementCssQuery;

    private ProductBlockSegmentor(){
    }

    public Element extractInfoContainer(Element productBlock) {
        return productBlock.selectFirst(infoElementCssQuery);
    }

    public Element extractListDescription(Element infoContainer) {
        return infoContainer.selectFirst(listDescriptionElementCssQuery);
    }
}
