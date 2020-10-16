package com.wine.to.up.winestyle.parser.service.service;

import org.jsoup.nodes.Element;

public interface MainPageParser {
    String parseName(Element el);
    Integer parseCropYear(String name);
    Float parsePrice(Element el);
    Double parseWinestyleRating(Element el);
    String parseManufacturer(Element el);
    String parseBrand(Element el);
    Float parseVolume(Element el);
    String parseStrength(Element el);
    String parseGrape(Element el);
    String[] parseCountryAndRegions(Element el);
    String[] parseColorAndSugar(Element el);
    String[] parseTypeColorSugar(Element el);
}
