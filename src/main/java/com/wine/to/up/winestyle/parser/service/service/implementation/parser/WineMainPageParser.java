package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WineMainPageParser extends MainPageParser {
    /**
     * Main page parsing
     * @param productElement HTML-блок, содержащий информацию о продукте.
     * @param builder Строитель сущности продукта.
     */
    @Override
    public void parseMainPageInfo(Element productElement, Wine.WineBuilder builder) {
        parseHeaderAndRightBlock(productElement, builder);

        // Block containing product's rest part of information
        Element infoContainer = productElement.selectFirst(".info-container");

        parseInfoContainer(infoContainer, builder);
    }

    /**
     * Product block header and right block parsing
     * @param itemBlock HTML-блок, содержащий информацию о продукте.
     * @param builder Строитель сущности продукта.
     */
    void parseHeaderAndRightBlock(Element itemBlock, Wine.WineBuilder builder) {
        String name;
        Integer cropYear;
        Float price;

        name = parseName(itemBlock);
        cropYear = parseCropYear(name);
        price = parsePrice(itemBlock);

        builder.name(name).cropYear(cropYear).price(price);
    }

    /**
     * Main page parsing
     * @param infoContainer HTML-блок, содержащий детальное описание продукта.
     * @param builder Строитель сущности продукта.
     */
    void parseInfoContainer(Element infoContainer, Wine.WineBuilder builder) {
        Double rating;
        String manufacturer;
        String brand;
        String country;
        String region;
        Float volume;
        String strength;
        String grape;
        String color;
        String sugar;

        manufacturer = parseManufacturer(infoContainer);
        brand = parseBrand(infoContainer);
        volume = parseVolume(infoContainer);
        strength = parseStrength(infoContainer);
        grape = parseGrape(infoContainer);
        rating = parseWinestyleRating(infoContainer);

        String[] countryAndRegions = parseCountryAndRegions(infoContainer);
        country = countryAndRegions[0];
        region = countryAndRegions[1];

        String[] colorAndSugar = parseColorAndSugar(infoContainer);
        color = colorAndSugar[0];
        sugar = colorAndSugar[1];

        builder
                .rating(rating).manufacturer(manufacturer).brand(brand).country(country).region(region)
                .volume(volume).strength(strength).grape(grape).color(color).sugar(sugar);
    }

    /**
     * Парсер винограда, свойство: год сбора.
     * @param name Контейнер, в котором лежит год сбора винограда.
     * @return Год сбора ИЛИ null, если его нет.
     */
    private Integer parseCropYear(String name) {
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
    private String[] parseColorAndSugar(Element el) {
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
