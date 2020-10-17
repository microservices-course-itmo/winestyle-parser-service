package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

@Slf4j
public abstract class MainPageParser implements com.wine.to.up.winestyle.parser.service.service.MainPageParser {
    /**
     * Парсер названия вина.
     * @param el Контейнер, в котором лежит название вина.
     * @return Название вина.
     */
    public String parseName(Element el){
        Element nameElement = el.selectFirst(".title");
        return nameElement.attr("data-prodname");
    }

    /**
     * Парсер винограда, свойство: год сбора.
     *
     * @param name Контейнер, в котором лежит год сбора винограда.
     * @return Год сбора ИЛИ null, если его нет.
     */
    public Integer parseCropYear(String name) {
        String[] titleInfo = name.split(", ");
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
     * @param el Контейнер, в котором лежит стоимость вина.
     * @return Стоимость вина ИЛИ null, если её нет.
     */
    public Float parsePrice(Element el){
        try {
            String priceValue = el.selectFirst(".price").ownText();
            priceValue = priceValue.replaceAll(" ", "");
            return Float.parseFloat(priceValue);
        } catch (Exception ex) {
            log.warn("product's price is not specified");
            return null;
        }
    }

    /**
     * Парсер рейтинга вина.
     * @param el Контейнер, в котором лежит рейтинг вина.
     * @return Рейтинг вина ИЛИ null, если его нет.
     */
    public Double parseWinestyleRating(Element el) {
        try {
            String ratingElement = el.selectFirst(".rating-text").child(1).text();
            return Double.parseDouble(ratingElement) / 2.;
        } catch (Exception ex) {
            log.warn("product's winestyle's rating is not specified");
            return null;
        }
    }

    /**
     * Парсер объема.
     * @param el Контейнер, в котором лежит объем вина.
     * @return Объем в мл ИЛИ null, если его нет.
     */
    public Float parseVolume(Element el) {
        try {
            Element volumeElement = el.selectFirst("label");
            String volumeValue = volumeElement.ownText();
            volumeValue = volumeValue.replaceAll("\\s[мл]+", "");

            float volume = Float.parseFloat(volumeValue);
            volume = (volume%1 == 0) ? volume / 1000 : volume;

            return volume;
        } catch (NullPointerException ex) {
            log.warn("product's volume is not specified");
            return null;
        }
    }

    /**
     * Парсер производителя вина.
     * @param el Контейнер, в котором лежит производитель вина.
     * @return Производитель ИЛИ null, если его нет.
     */
    public String parseManufacturer(Element el) {
        try {
            Element manufacturerElement = el.selectFirst("span:contains(Прои)").nextElementSibling();
            return manufacturerElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's manufacturer is not specified");
            return null;
        }
    }

    /**
     * Парсер бренда вина.
     * @param el Контейнер, в котором лежит бренд вина.
     * @return Бренд ИЛИ null, если его нет.
     */
    public String parseBrand(Element el) {
        try {
            Element brandElement = el.selectFirst("span:contains(Брен)").nextElementSibling();
            return brandElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's brand is not specified");
            return null;
        }
    }

    /**
     * Парсер свойств: страна и регион.
     * @param el Контейнер, в котором лежит описание происхождения вина.
     * @return  Свойства: страна и регион в виде массива из двух элементов, которые мы достали, ИЛИ массив из двух Null, если таковых нет.
     */
    public String[] parseCountryAndRegions(Element el){
        try {
            Element countryElement = el.selectFirst("span:contains(Рег)").nextElementSibling();
            String country = countryElement.text();

            Element regionElement = countryElement.nextElementSibling();
            String allRegions = regionElement.text();

            // Add region names to the resulting string as long as there are elements containing them
            String nextRegion;
            Element nextElement;

            while (regionElement.nextElementSibling() != null) {
                nextElement = regionElement.nextElementSibling();
                nextRegion = nextElement.text();
                allRegions = String.join(", ", new String[]{allRegions, nextRegion});
                regionElement = nextElement;
            }

            return new String[] {country, allRegions};
        } catch (NullPointerException ex) {
            log.warn("product's country and region are not specified");
            return new String[] {null,null};
        }
    }

    /**
     * Парсер крепости вина.
     * @param el Контейнер, в котором лежит описание крепости вина.
     * @return Крепость ИЛИ null, если совйства нет.
     */
    public String parseStrength(Element el) {
        try {
            Element strengthElement = el.selectFirst("span:contains(Креп)").nextElementSibling();
            return strengthElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's strength is not specified");
            return null;
        }
    }

    /**
     * Парсер сорта винограда.
     * @param el Контейнер, в котором лежит описание сорта винограда.
     * @return Объединенная строка сортов винограда ИЛИ null, если их нет.
     */
    public String parseGrape(Element el) {
        try {
            Element grapeElement = el.selectFirst("span:contains(Сорт)").nextElementSibling();
            String allGrape = grapeElement.text();

            // Add grape sorts to the resulting string as long as there are elements containing them
            String nextGrape;
            Element nextElement;

            while (grapeElement.nextElementSibling() != null) {
                nextElement = grapeElement.nextElementSibling();
                nextGrape = nextElement.text();
                allGrape = String.join(", ", new String[]{allGrape, nextGrape});
                grapeElement = nextElement;
            }

            return grapeElement.text();
        } catch (NullPointerException ex) {
            log.warn("product's grape sort is not specified");
            return null;
        }
    }
}
