package com.wine.to.up.winestyle.parser.service.service.implementation.parser.parsing;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Qualifier("sparklingParsingService")
@Component
public class SparklingParsing extends WineParsing {
    private Element typeAndColorElement;
    private String[] typeAndColor;

    /**
     * Парсер типа игристого напитка.
     * @return Тип игристого напитка ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseType() {
        try {
            typeAndColorElement = infoContainer.selectFirst("span:contains(Игристое вино/шампанское:)").nextElementSibling();
            typeAndColor = typeAndColorElement.text().split("-");
            return typeAndColor[0];
        } catch (NullPointerException ex) {
            log.warn("product's sparkling type is not specified");
            return null;
        }
    }

    /**
     * Парсер оттенка.
     * @return Оттенок ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseColor() {
        try {
            return typeAndColor[1];
        } catch (ArrayIndexOutOfBoundsException ex) {
            log.warn("product's color is not specified");
            return null;
        }
    }

    /**
     * Парсер сладости/сухости.
     * @return Сладость/сухость ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseSugar() {
        try {
            return typeAndColorElement.nextElementSibling().text();
        } catch (NullPointerException ex) {
            log.warn("product's sugar is not specified");
            return null;
        }
    }
}
