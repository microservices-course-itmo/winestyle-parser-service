package com.wine.to.up.winestyle.parser.service.service;

import org.jsoup.nodes.Element;

public interface ParsingService {
    String parseName();

    String parseImageUrl();

    String parseUrl();

    Integer parseCropYear();

    Float parsePrice();

    Float parseWinestyleRating();

    Float parseVolume();

    String parseManufacturer();

    String parseBrand();

    String parseCountry();

    String parseRegion();

    String parseStrength();

    String parseGrape();

    String parseType(Boolean isSparkling);

    String parseColor();

    String parseSugar();

    String parseTaste();

    String parseAroma();

    String parseFoodPairing();

    String parseDescription();

    void setProductBlock(Element productBlock);

    void setInfoContainer(Element infoContainer);

    void setListDescription(Element listDescription);

    void setLeftBlock(Element leftBlock);

    void setArticlesBlock(Element articlesBlock);

    void setDescriptionBlock(Element descriptionBlock);
}
