package com.wine.to.up.winestyle.parser.service.service.implementation.parser.parsing;

import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WineParsing implements ParsingService {
    @Setter
    private Element productBlock;
    @Setter
    Element infoContainer;
    @Setter
    private Element leftBlock;
    @Setter
    private Element articlesBlock;
    @Setter
    private Element descriptionBlock;

    private String name;
    private Element colorElement;
    private Element countryElement;

    /**
     * Парсер названия вина.
     *
     * @return Название вина.
     */
    @Override
    public String parseName() {
        Element nameElement = productBlock.selectFirst(".title");
        name = nameElement.attr("data-prodname");
        return name;
    }

    @Override
    public String parseUrl() {
        return productBlock.selectFirst("a").attr("href");
    }

    /**
     * Парсер картинки.
     *
     * @return Ссылка на картинку, которую мы достали или Null, если картинки нет.
     */
    @Override
    public String parseImageUrl() {
        try {
            Element imageElement = leftBlock.selectFirst("a.img-container");
            return imageElement.attr("href");
        } catch (NullPointerException ex) {
            log.warn("product's image is not specified");
            return null;
        }
    }

    /**
     * Парсер винограда, свойство: год сбора.
     *
     * @return Год сбора ИЛИ null, если его нет.
     */
    @Override
    public Integer parseCropYear() {
        String[] titleInfo = name.split(",? ");
        // Checks each word in the name for year format matching
        for (String word : titleInfo) {
            if (word.matches("\\d{4}")) {
                return Integer.parseInt(word);
            }
        }
        log.warn("product's crop year is not specified");
        return null;
    }

    /**
     * Парсер цены вина.
     *
     * @return Стоимость вина ИЛИ null, если её нет.
     */
    @Override
    public Float parsePrice() {
        try {
            String priceValue = productBlock.selectFirst(".price").ownText();
            priceValue = priceValue.replaceAll(" ", "");
            return Float.parseFloat(priceValue);
        } catch (Exception ex) {
            log.warn("product's price is not specified");
            return null;
        }
    }

    /**
     * Парсер рейтинга вина.
     *
     * @return Рейтинг вина ИЛИ null, если его нет.
     */
    @Override
    public Double parseWinestyleRating() {
        try {
            String rating = infoContainer.selectFirst(".info-container meta[itemprop=ratingValue]").attr("content");
            return Double.parseDouble(rating) / 2.;
        } catch (Exception ex) {
            log.warn("product's winestyle's rating is not specified");
            return null;
        }
    }

    /**
     * Парсер объема.
     *
     * @return Объем в мл ИЛИ null, если его нет.
     */
    @Override
    public Float parseVolume() {
        try {
            Element volumeElement = infoContainer.selectFirst("label");
            String volumeValue = volumeElement.ownText();
            volumeValue = volumeValue.replaceAll("\\s[мл]+", "");

            float volume = Float.parseFloat(volumeValue);
            volume = (volume % 1 == 0) ? volume / 1000 : volume;

            return volume;
        } catch (NullPointerException ex) {
            log.warn("product's volume is not specified");
            return null;
        }
    }

    /**
     * Парсер производителя вина.
     *
     * @return Производитель ИЛИ null, если его нет.
     */
    @Override
    public String parseManufacturer() {
        try {
            Element manufacturerElement = infoContainer.selectFirst("span:contains(Производитель:)").nextElementSibling();
            return manufacturerElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's manufacturer is not specified");
            return null;
        }
    }

    /**
     * Парсер бренда вина.
     *
     * @return Бренд ИЛИ null, если его нет.
     */
    @Override
    public String parseBrand() {
        try {
            Element brandElement = infoContainer.selectFirst("span:contains(Брен)").nextElementSibling();
            return brandElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's brand is not specified");
            return null;
        }
    }

    /**
     * Парсер страны происхождения винограда.
     *
     * @return Страна ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseCountry() {
        try {
            countryElement = infoContainer.selectFirst("span:contains(Рег)").nextElementSibling();
            return countryElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's country is not specified");
            return null;
        }
    }

    /**
     * Парсер регионов происхождения винограда.
     *
     * @return Регионы ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseRegion() {
        try {
            Element regionElement = countryElement.nextElementSibling();
            return parseFieldsSequence(regionElement);
        } catch (NullPointerException ex) {
            log.warn("product's region is not specified");
            return null;
        }
    }

    /**
     * Парсер крепости вина.
     *
     * @return Крепость ИЛИ null, если свойства нет.
     */
    @Override
    public String parseStrength() {
        try {
            Element strengthElement = infoContainer.selectFirst("span:contains(Креп)").nextElementSibling();
            return strengthElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's strength is not specified");
            return null;
        }
    }

    /**
     * Парсер сорта винограда.
     *
     * @return Объединенная строка сортов винограда ИЛИ null, если их нет.
     */
    @Override
    public String parseGrape() {
        try {
            Element grapeElement = infoContainer.selectFirst("span:contains(Сорт)").nextElementSibling();
            return parseFieldsSequence(grapeElement);
        } catch (NullPointerException ex) {
            log.warn("product's grape sort is not specified");
            return null;
        }
    }

    @Override
    public String parseType() {
        throw new UnsupportedOperationException(
                "Operation is not supported for wine."
        );
    }

    /**
     * Парсер оттенка.
     * @return Оттенок ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseColor() {
        try {
            colorElement = infoContainer.selectFirst("span:contains(Вино:)").nextElementSibling();
            return colorElement.text();
        } catch (NullPointerException ex) {
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
            return colorElement.nextElementSibling().text();
        } catch (NullPointerException ex) {
            log.warn("product's sugar is not specified");
            return null;
        }
    }

    /**
     * Парсер вкуса вина.
     *
     * @return Вкус вина ИЛИ null, если нет его описания.
     */
    @Override
    public String parseTaste() {
        try {
            Element tasteElement = articlesBlock.selectFirst("span:contains(Вкус)").nextElementSibling();
            return tasteElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's taste is not specified");
            return null;
        }
    }

    /**
     * Парсер аромата вина.
     *
     * @return Аромат ИЛИ null, если нет его описания.
     */
    @Override
    public String parseAroma() {
        try {
            Element aromaElement = articlesBlock.selectFirst("span:contains(Аром)").nextElementSibling();
            return aromaElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's aroma is not specified");
            return null;
        }
    }

    /**
     * Парсер сочетания вина с блюдами.
     *
     * @return Строку сочетаний ИЛИ null, если их нет.
     */
    @Override
    public String parseFoodPairing() {
        try {
            Element foodPairingElement = articlesBlock.selectFirst("span:contains(Гаст)").nextElementSibling();
            return foodPairingElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's food pairing is not specified");
            return null;
        }
    }

    /**
     * Парсер описания.
     *
     * @return Описание, которое мы достали, ИЛИ Null, если описания нет.
     */
    @Override
    public String parseDescription() {
        try {
            Element descriptionElement = descriptionBlock.selectFirst(".description-block");
            return descriptionElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's description is not specified");
            return null;
        }
    }

    private String parseFieldsSequence(Element firstSequenceElement) {
        String allFields = firstSequenceElement.text();

        // Add fields to the resulting string as long as there are elements containing them
        String nextRegion;
        Element nextElement;
        while (firstSequenceElement.nextElementSibling() != null) {
            nextElement = firstSequenceElement.nextElementSibling();
            nextRegion = nextElement.text();
            allFields = String.join(", ", new String[]{allFields, nextRegion});
            firstSequenceElement = nextElement;
        }

        return allFields;
    }
}
