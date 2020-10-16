package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SparklingMainPageParser extends MainPageParser {
    /**
     * Main page parsing
     * @param productElement HTML-блок, содержащий информацию о продукте.
     * @param builder Строитель сущности продукта.
     */
    @Override
    public void parseMainPageInfo(Element productElement, Sparkling.SparklingBuilder builder) {
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
    void parseHeaderAndRightBlock(Element itemBlock, Sparkling.SparklingBuilder builder) {
        String name;
        Float price;

        name = parseName(itemBlock);
        price = parsePrice(itemBlock);

        builder.name(name).price(price);
    }

    /**
     * Main page parsing
     * @param infoContainer HTML-блок, содержащий детальное описание продукта.
     * @param builder Строитель сущности продукта.
     */
    void parseInfoContainer(Element infoContainer, Sparkling.SparklingBuilder builder) {
        Double rating;
        String manufacturer;
        String brand;
        String country;
        String region;
        Float volume;
        String strength;
        String grape;
        String type;
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

        String[] typeColorSugar = parseTypeColorSugar(infoContainer);
        type = typeColorSugar[0];
        color = typeColorSugar[1];
        sugar = typeColorSugar[2];

        builder
                .rating(rating).manufacturer(manufacturer).brand(brand).country(country).region(region)
                .volume(volume).strength(strength).grape(grape).type(type).color(color).sugar(sugar);
    }

    /**
     * Парсер свойств: оттенок и сладость/сухость.
     * @param el Контейнер, в котором лежит описание свойств вина.
     * @return Свойства: оттенок и сладость/сухость в виде массива из двух элементов, которые мы достали, ИЛИ массив из двух Null, если таковых нет.
     */
    String[] parseTypeColorSugar(Element el) {
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
