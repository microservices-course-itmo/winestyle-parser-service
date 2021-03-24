package com.wine.to.up.winestyle.parser.service.service.implementation.parser.parsing;

import com.wine.to.up.winestyle.parser.service.service.Parser;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AlcoholParser implements Parser {
    @Setter
    private Element productBlock;
    @Setter
    private Element infoContainer;
    @Setter
    private Element listDescription;
    @Setter
    private Element leftBlock;
    @Setter
    private Element articlesBlock;
    @Setter
    private Element descriptionBlock;

    @Value("${spring.jsoup.parsing.css.query.name}")
    private String nameElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.url}")
    private String urlElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.image-url}")
    private String imageUrlElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.price}")
    private String priceElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.winestyle-rating}")
    private String winestyleRatingElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.volume}")
    private String volumeElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.manufacturer}")
    private String manufacturerElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.brand}")
    private String brandElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.country}")
    private String countryElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.strength}")
    private String strengthElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.grape}")
    private String grapeElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.type}")
    private String typeElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.availability}")
    private String availabilityElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.taste}")
    private String tasteElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.aroma}")
    private String aromaElementCssQuery;
    @Value("${spring.jsoup.parsing.css.query.food-pairing}")
    private String foodPairingCssQuery;
    @Value("${spring.jsoup.parsing.css.query.description}")
    private String descriptionCssQuery;

    @Value("${spring.jsoup.parsing.css.attr.name}")
    private String namePropertyCssAttr;
    @Value("${spring.jsoup.parsing.css.attr.image-url}")
    private String imageUrlPropertyCssAttr;
    @Value("${spring.jsoup.parsing.css.attr.winestyle-rating}")
    private String winestyleRatingPropertyCssAttr;
    @Value("${spring.jsoup.parsing.css.attr.availability}")
    private String availabilityPropertyCssAttr;

    private String name;
    private String url;
    private String region;
    private boolean isRegionPresented = true;
    private boolean isColorPresented = true;
    private boolean isSugarPresented = true;
    private String colorAndSugar;

    /**
     * Парсер названия алкоголя.
     *
     * @return Название алкоголя.
     */
    @Override
    public String parseName() {
        Element nameElement = productBlock.selectFirst(nameElementCssQuery);
        name = nameElement.attr(namePropertyCssAttr);
        return name;
    }

    /**
     * Парсер адреса страницы алкоголя.
     *
     * @return Адрес страницы алкоголя.
     */
    @Override
    public String parseUrl() {
        url = productBlock.selectFirst(urlElementCssQuery).attr("href");
        return url;
    }

    /**
     * Парсер свойств: Тип и отеннок вина/игристого.
     *
     * @return Тип напитка ИЛИ массив из двух Null, если свойств нет.
     */
    @Override
    public String parseType(boolean isSparkling) {
        try {
            Element typeAndColorElement = listDescription.selectFirst(typeElementCssQuery);
            Element typeAndColorParent = typeAndColorElement.parent();
            typeAndColorElement.remove();
            String typeColorSugar = typeAndColorParent.text();
            typeAndColorParent.remove();
            if (isSparkling) {
                return parseSparklingType(typeColorSugar);
            } else {
                return parseWineType(typeColorSugar);
            }
        } catch (NullPointerException e) {
            isColorPresented = false;
            isSugarPresented = false;

            log.warn("{}: product's color and sugar are not specified", url);

            if (isSparkling) {
                if (region.equals("Шампань")) {
                    return "Шампанское";
                } else {
                    return "Игристое";
                }
            } else {
                return "Вино";
            }
        }
    }

    /**
     * Парсер картинки.
     *
     * @return Ссылка на картинку, которую мы достали или Null, если картинки нет.
     */
    @Override
    public Optional<String> parseImageUrl() {
        try {
            Element imageElement = leftBlock.selectFirst(imageUrlElementCssQuery);
            return Optional.of(imageElement.attr(imageUrlPropertyCssAttr));
        } catch (NullPointerException ex) {
            log.warn("{}: product's image is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер винограда, свойство: год сбора.
     *
     * @return Год сбора ИЛИ null, если его нет.
     */
    @Override
    public Optional<Integer> parseCropYear() {
        String[] titleInfo = name.split(",? ");
        // Checks each word in the name for year format matching
        for (String word : titleInfo) {
            if (word.matches("^\\d{4}$")) {
                return Optional.of(Integer.parseInt(word));
            }
        }
        log.warn("{}: product's crop year is not specified", url);
        return Optional.empty();
    }

    /**
     * Парсер цены вина.
     *
     * @return Стоимость вина ИЛИ null, если её нет.
     */
    @Override
    public Optional<Float> parsePrice() {
        try {
            String priceValue = productBlock.selectFirst(priceElementCssQuery).ownText();
            priceValue = priceValue.replace(" ", "");
            return Optional.of(Float.parseFloat(priceValue));
        } catch (Exception ex) {
            log.warn("{}: product's price is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер рейтинга вина.
     *
     * @return Рейтинг вина ИЛИ null, если его нет.
     */
    @Override
    public Optional<Float> parseWinestyleRating() {
        try {
            String rating = infoContainer.selectFirst(winestyleRatingElementCssQuery).attr(winestyleRatingPropertyCssAttr);
            return Optional.of(Float.parseFloat(rating) / 2.f);
        } catch (Exception ex) {
            log.warn("{}: product's winestyle's rating is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер объема.
     *
     * @return Объем в мл ИЛИ null, если его нет.
     */
    @Override
    public Optional<Float> parseVolume() {
        try {
            Element volumeElement = infoContainer.selectFirst(volumeElementCssQuery);
            String volumeValue = volumeElement.ownText();
            volumeValue = volumeValue.replaceAll("\\s[мл]+", "");

            float volume = Float.parseFloat(volumeValue);
            volume = (volume % 1 == 0) ? volume / 1000 : volume;

            return Optional.of(volume);
        } catch (NullPointerException ex) {
            log.warn("{}: product's volume is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер производителя вина.
     *
     * @return Производитель ИЛИ null, если его нет.
     */
    @Override
    public Optional<String> parseManufacturer() {
        try {
            Element manufacturerElement = listDescription.selectFirst(manufacturerElementCssQuery);
            Element manufacturerParent = manufacturerElement.parent();
            manufacturerElement.remove();
            String manufacturer = manufacturerParent.text();
            manufacturerParent.remove();
            return Optional.of(manufacturer);
        } catch (NullPointerException ex) {
            log.warn("{}: product's manufacturer is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер бренда вина.
     *
     * @return Бренд ИЛИ null, если его нет.
     */
    @Override
    public Optional<String> parseBrand() {
        try {
            Element brandElement = listDescription.selectFirst(brandElementCssQuery);
            Element brandParent = brandElement.parent();
            brandElement.remove();
            String brand = brandParent.text();
            brandParent.remove();
            return Optional.of(brand);
        } catch (NullPointerException ex) {
            log.warn("{}: product's brand is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер страны происхождения винограда.
     *
     * @return Страна ИЛИ Null, если свойства нет.
     */
    @Override
    public Optional<String> parseCountry() {
        try {
            Element countryElement = listDescription.selectFirst(countryElementCssQuery);
            Element countryParent = countryElement.parent();
            countryElement.remove();
            String countryAndRegion = countryParent.text();
            countryParent.remove();
            int indexOfDelim = countryAndRegion.indexOf(", ");
            if (indexOfDelim >= 0) {
                String country = countryAndRegion.substring(0, indexOfDelim);
                region = countryAndRegion.substring(indexOfDelim + 2);
                return Optional.of(country);
            } else {
                isRegionPresented = false;
                return Optional.of(countryAndRegion);
            }
        } catch (NullPointerException ex) {
            log.warn("{}: product's country and region are not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер регионов происхождения винограда.
     *
     * @return Регионы ИЛИ Null, если свойства нет.
     */
    @Override
    public Optional<String> parseRegion() {
        if (isRegionPresented) {
            return Optional.of(region);
        } else {
            log.warn("{}: product's region is not specified", url);
            isRegionPresented = true;
            return Optional.empty();
        }
    }

    /**
     * Парсер крепости вина.
     *
     * @return Крепость ИЛИ null, если свойства нет.
     */
    @Override
    public Optional<Float> parseStrength() {
        try {
            Element strengthElement = listDescription.selectFirst(strengthElementCssQuery);
            Element strengthParent = strengthElement.parent();
            strengthElement.remove();
            String strength = strengthParent.text();
            strengthParent.remove();
            return Optional.of(Float.parseFloat(strength.substring(0, strength.length() - 1)));
        } catch (Exception ex) {
            log.warn("{}: product's strength is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер сорта винограда.
     *
     * @return Объединенная строка сортов винограда ИЛИ null, если их нет.
     */
    @Override
    public Optional<String> parseGrape() {
        try {
            Element grapeElement = listDescription.selectFirst(grapeElementCssQuery);
            Element grapeParent = grapeElement.parent();
            grapeElement.remove();
            String grape = grapeParent.text();
            grapeParent.remove();
            return Optional.of(grape);
        } catch (NullPointerException ex) {
            log.warn("{}: product's grape sort is not specified", url);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> parseColor() {
        if (isColorPresented) {
            int indexOfDelim = colorAndSugar.indexOf(", ");
            if (indexOfDelim >= 0) {
                String color = colorAndSugar.substring(0, indexOfDelim);
                colorAndSugar = colorAndSugar.substring(indexOfDelim + 2);
                return Optional.of(color.substring(0, 1).toUpperCase() + color.substring(1));
            } else {
                isSugarPresented = false;
                return Optional.of(colorAndSugar.substring(0, 1).toUpperCase() + colorAndSugar.substring(1));
            }
        } else {
            log.warn("{}: sparkling's color is not specified", url);
            isColorPresented = true;
            return Optional.empty();
        }
    }

    /**
     * Парсер сладости/сухости.
     *
     * @return Сладость/сухость ИЛИ Null, если свойства нет.
     */
    @Override
    public Optional<String> parseSugar() {
        if (isSugarPresented) {
            return Optional.of(colorAndSugar);
        } else {
            log.warn("{}: product's sugar is not specified", url);
            isSugarPresented = true;
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> parseAvailability() {
        try {
            Element availabilityElement = productBlock.selectFirst(availabilityElementCssQuery);
            String availabilityAttr = availabilityElement.attr(availabilityPropertyCssAttr);
            String availabilityValue = availabilityAttr.substring(availabilityAttr.length() - 1) ;
            return Optional.of(availabilityValue.matches("[13]"));
        } catch (Exception ex) {
            log.warn("{}: product's in stock availability is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер вкуса вина.
     *
     * @return Вкус вина ИЛИ null, если нет его описания.
     */
    @Override
    public Optional<String> parseTaste() {
        try {
            Element tasteElement = articlesBlock.selectFirst(tasteElementCssQuery).nextElementSibling();
            return Optional.of(tasteElement.text());
        } catch (NullPointerException ex) {
            log.warn("{}: product's taste is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер аромата вина.
     *
     * @return Аромат ИЛИ null, если нет его описания.
     */
    @Override
    public Optional<String> parseAroma() {
        try {
            Element aromaElement = articlesBlock.selectFirst(aromaElementCssQuery).nextElementSibling();
            return Optional.of(aromaElement.text());
        } catch (NullPointerException ex) {
            log.warn("{}: product's aroma is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер сочетания вина с блюдами.
     *
     * @return Строку сочетаний ИЛИ null, если их нет.
     */
    @Override
    public Optional<String> parseFoodPairing() {
        try {
            Element foodPairingElement = articlesBlock.selectFirst(foodPairingCssQuery).nextElementSibling();
            return Optional.of(foodPairingElement.text());
        } catch (NullPointerException ex) {
            log.warn("{}: product's food pairing is not specified", url);
            return Optional.empty();
        }
    }

    /**
     * Парсер описания.
     *
     * @return Описание, которое мы достали, ИЛИ Null, если описания нет.
     */
    @Override
    public Optional<String> parseDescription() {
        try {
            Element descriptionElement = descriptionBlock.selectFirst(descriptionCssQuery);
            return Optional.of(descriptionElement.text());
        } catch (NullPointerException ex) {
            log.warn("{}: product's description is not specified", url);
            return Optional.empty();
        }
    }

    private String parseWineType(String typeColorSugar) {
        String type;
        int indexOfDelim = typeColorSugar.indexOf(", ");
        if (indexOfDelim >= 0) {
            type = typeColorSugar.substring(0, indexOfDelim);
        } else {
            isSugarPresented = false;
            type = typeColorSugar;
        }
        if (type.matches("^(?!С|Пол|Р|Б|О|Г|Кра).+")) {
            colorAndSugar = typeColorSugar.substring(indexOfDelim + 2);
            isColorPresented = false;
            return type;
        } else {
            colorAndSugar = typeColorSugar;
            return "Вино";
        }
    }

    private String parseSparklingType(String typeColorSugar) {
        String type;
        int indexOfDelim = typeColorSugar.indexOf("-");
        if (indexOfDelim >= 0) {
            type = typeColorSugar.substring(0, indexOfDelim);
            colorAndSugar = typeColorSugar.substring(indexOfDelim + 1);
            return type;
        } else if (typeColorSugar.matches("^[ШИ].+")) {
            isColorPresented = false;
            indexOfDelim = typeColorSugar.indexOf(", ");
            if (indexOfDelim >= 0) {
                type = typeColorSugar.substring(0, indexOfDelim);
                colorAndSugar = typeColorSugar.substring(indexOfDelim + 2);
                return type;
            } else {
                isSugarPresented = false;
                return typeColorSugar;
            }
        } else {
            isColorPresented = false;
            if (region.equals("Шампань")) {
                return "Шампанское";
            } else {
                return "Игристое";
            }
        }
    }
}
