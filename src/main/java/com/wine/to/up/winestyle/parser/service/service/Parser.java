package com.wine.to.up.winestyle.parser.service.service;

import org.jsoup.nodes.Element;

import java.util.Optional;

public interface Parser {
    String parseName();

    String parseUrl();

    String parseType(boolean isSparkling);

    Optional<String> parseImageUrl();

    Optional<Integer> parseCropYear();

    Optional<Float> parsePrice();

    Optional<Float> parseWinestyleRating();

    Optional<Float> parseVolume();

    Optional<String> parseManufacturer();

    Optional<String> parseBrand();

    Optional<String> parseCountry();

    Optional<String> parseRegion();

    Optional<Float> parseStrength();

    Optional<String> parseGrape();

    Optional<String> parseColor();

    Optional<String> parseSugar();

    Optional<String> parseAvailability();

    Optional<String> parseTaste();

    Optional<String> parseAroma();

    Optional<String> parseFoodPairing();

    Optional<String> parseDescription();

    void setProductBlock(Element productBlock);

    void setInfoContainer(Element infoContainer);

    void setListDescription(Element listDescription);

    void setLeftBlock(Element leftBlock);

    void setArticlesBlock(Element articlesBlock);

    void setDescriptionBlock(Element descriptionBlock);
}
