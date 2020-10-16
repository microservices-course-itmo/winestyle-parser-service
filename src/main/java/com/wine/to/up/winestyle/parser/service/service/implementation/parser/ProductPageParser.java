package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductPageParser implements com.wine.to.up.winestyle.parser.service.service.ProductPageParser {
    /**
     * Парсер картинки.
     * @param el Контейнер, в котором лежит ссылка на картинку.
     * @return Ссылка на картинку, которую мы достали, ИЛИ Null, если картинки нет.
     */
    public String parseImageUrl(Element el) {
        try {
            Element imageElement = el.selectFirst("a.img-container");
            return imageElement.attr("href");
        } catch (NullPointerException ex) {
            log.warn("product's image is not specified");
            return null;
        }
    }

    /**
     * Парсер описания.
     * @param el Контейнер, в котором лежит описание.
     * @return Описание, которое мы достали, ИЛИ Null, если описания нет.
     */
    public String parseDescription(Element el) {
        try {
            Element descriptionElement = el.selectFirst(".description-block");
            return descriptionElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's description is not specified");
            return null;
        }
    }

    /**
     * Парсер вкуса вина.
     * @param el Контейнер, в котором лежит описание вкуса вина.
     * @return Вкус вина ИЛИ null, если нет его описания.
     */
    public String parseTaste(Element el) {
        try {
            Element tasteElement = el.selectFirst("span:contains(Вкус)").nextElementSibling();
            return tasteElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's taste is not specified");
            return null;
        }
    }

    /**
     * Парсер аромата вина.
     * @param el Контейнер, в котором описан аромат вина.
     * @return Аромат ИЛИ null, если нет его описания.
     */
    public String parseAroma(Element el) {
        try {
            Element aromaElement = el.selectFirst("span:contains(Аром)").nextElementSibling();
            return aromaElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's aroma is not specified");
            return null;
        }
    }

    /**
     * Парсер сочетания вина с блюдами.
     * @param el Контейнер, в котором перечислены хорошие сочетания вина с блюдами.
     * @return Строку сочетаний ИЛИ null, если их нет.
     */
    public String parseFoodPairing(Element el) {
        try {
            Element foodPairingElement = el.selectFirst("span:contains(Гаст)").nextElementSibling();
            return foodPairingElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's food pairing is not specified");
            return null;
        }
    }
}
