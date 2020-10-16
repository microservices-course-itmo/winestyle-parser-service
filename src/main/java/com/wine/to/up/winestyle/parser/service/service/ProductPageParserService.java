package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import org.jsoup.nodes.Element;

public interface ProductPageParserService {
    void parseProductPageInfo(Element productPageElement, Wine.WineBuilder builder);
    void parseProductPageInfo(Element productPageElement, Sparkling.SparklingBuilder builder);
}
