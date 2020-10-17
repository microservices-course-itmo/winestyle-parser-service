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
     * Парсер свойств: оттенок и сладость/сухость.
     *
     * @param el Контейнер, в котором лежит описание свойств вина.
     * @return Свойства: оттенок и сладость/сухость в виде массива из дыух элементов, которые мы достали, ИЛИ массив из двух Null, если таковых нет.
     */
    public String[] parseColorAndSugar(Element el) {
        try {
            Element colorElement = el.selectFirst("span:contains(Вино:)").nextElementSibling();
            try {
                Element sugarElement = colorElement.nextElementSibling();
                return new String[] {colorElement.text(), sugarElement.text()};
            } catch (NullPointerException ex) {
                log.warn("product's sugar is not specified");
                return new String[] {colorElement.text(), null};
            }
        } catch (NullPointerException ex) {
            log.warn("product's color and sugar are not specified");
            return new String[] {null, null};
        }
    }
}
