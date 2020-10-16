package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WineMainPageParser extends MainPageParser {
    @Override
    public String[] parseTypeColorSugar(Element el) {
        throw new UnsupportedOperationException(
                "Operation is not supported. For wine, please, use parseColorAndSugar method instead."
        );
    }

    /**
     * Парсер винограда, свойство: год сбора.
     * @param name Контейнер, в котором лежит год сбора винограда.
     * @return Год сбора ИЛИ null, если его нет.
     */
    public Integer parseCropYear(String name) {
        try {
            String[] titleInfo = name.split(", ");
            String lastSubstr = titleInfo[titleInfo.length - 1];
            // Check the last substring for year format matching
            if (lastSubstr.matches("\\d{4}")) {
                return Integer.parseInt(lastSubstr);
            } else {
                log.warn("product's crop year is not specified");
                return null;
            }
        } catch (NullPointerException ex) {
            log.warn("product's crop year is not specified");
            return null;
        }
    }

    /**
     * Парсер свойств: оттенок и сладость/сухость.
     * @param el Контейнер, в котором лежит описание свойств вина.
     * @return Свойства: оттенок и сладость/сухость в виде массива из дыух элементов, которые мы достали, ИЛИ массив из двух Null, если таковых нет.
     */
    public String[] parseColorAndSugar(Element el) {
        try {
            Element colorElement = el.selectFirst("span:contains(Вино:)").nextElementSibling();
            Element sugarElement = colorElement.nextElementSibling();
            return new String[] {colorElement.text(),sugarElement.text()};
        } catch (NullPointerException ex) {
            log.warn("product's color and sugar are not specified");
            return new String[] {null, null};
        }
    }
}
