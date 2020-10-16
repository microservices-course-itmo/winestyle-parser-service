package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Qualifier("sparklingMainPageParserService")
@Component
public class SparklingMainPageParser extends MainPageParser {
    @Override
    public Integer parseCropYear(String name) {
        throw new UnsupportedOperationException(
                "Operation is not supported. Sparkling drinks has no crop year property."
        );
    }

    @Override
    public String[] parseColorAndSugar(Element el) {
        throw new UnsupportedOperationException(
                "Operation is not supported. For sparkling drinks, please, use parseTypeColorSugar method instead."
        );
    }

    /**
     * Парсер свойств: оттенок и сладость/сухость.
     * @param el Контейнер, в котором лежит описание свойств вина.
     * @return Свойства: оттенок и сладость/сухость в виде массива из двух элементов, которые мы достали, ИЛИ массив из двух Null, если таковых нет.
     */
    public String[] parseTypeColorSugar(Element el) {
        try {
            Element typeAndColorElement = el.selectFirst("span:contains(Игристое вино/шампанское:)").nextElementSibling();
            Element sugarElement;
            String[] typeAndColor;
            try {
                sugarElement = typeAndColorElement.nextElementSibling();
                typeAndColor = typeAndColorElement.text().split("-");
                try {
                    return new String[]{typeAndColor[0], typeAndColor[1], sugarElement.text()};
                } catch (ArrayIndexOutOfBoundsException ex) {
                    log.warn("product's color is not specified");
                    return new String[]{typeAndColor[0], null, sugarElement.text()};
                }
            } catch (NullPointerException ex) {
                log.warn("product's sugar is not specified");
                typeAndColor = typeAndColorElement.text().split("-");

                return new String[]{typeAndColor[0], typeAndColor[1], null};
            }
        } catch (NullPointerException ex) {
            log.warn("product's type, color and sugar are not specified");

            return new String[] {null, null, null};
        }
    }
}
