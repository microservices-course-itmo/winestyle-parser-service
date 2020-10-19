package com.wine.to.up.winestyle.parser.service.service;

import org.jsoup.nodes.Element;

public interface ParsingService {
    String parseName();
    String parseImageUrl();
    String parseUrl();
    Integer parseCropYear();
    Float parsePrice();
    Double parseWinestyleRating();
    Float parseVolume();
    String parseManufacturer();
    String parseBrand();
    String parseCountry();
    String parseRegion();
    String parseStrength();
    String parseGrape();
    String[] parseTypeAndColor();
    String parseSugar();
    String parseTaste();
    String parseAroma();
    String parseFoodPairing();
    String parseDescription();

    void setProductBlock(Element productBlock);
    void setInfoContainer(Element infoContainer);
    void setLeftBlock(Element leftBlock);
    void setArticlesBlock(Element articlesBlock);
    void setDescriptionBlock(Element descriptionBlock);
}
