package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import org.jsoup.nodes.Element;

public interface MainPageParserService {
    void parseMainPageInfo(Element productElement, Wine.WineBuilder builder);
    void parseMainPageInfo(Element productElement, Sparkling.SparklingBuilder builder);
    Float parsePrice(Element el);
    Double parseWinestyleRating(Element el);
}
