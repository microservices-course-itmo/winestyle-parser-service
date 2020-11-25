package com.wine.to.up.winestyle.parser.service.service.implementation.parser.parsing;

import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlcoholParsing implements ParsingService {
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
        Element nameElement = productBlock.selectFirst(".title");
        name = nameElement.attr("data-prodname");
        return name;
    }

    /**
     * Парсер адреса страницы алкоголя.
     *
     * @return Адрес страницы алкоголя.
     */
    @Override
    public String parseUrl() {
        url = productBlock.selectFirst("a").attr("href");
        return url;
    }

    /**
     * Парсер картинки.
     * @return Ссылка на картинку, которую мы достали или Null, если картинки нет.
     */
    @Override
    public String parseImageUrl() {
        try {
            Element imageElement = leftBlock.selectFirst("a.img-container");
            return imageElement.attr("href");
        } catch (NullPointerException ex) {
            log.warn("{}: product's image is not specified", url);
            return null;
        }
    }

    /**
     * Парсер винограда, свойство: год сбора.
     * @return Год сбора ИЛИ null, если его нет.
     */
    @Override
    public Integer parseCropYear() {
        String[] titleInfo = name.split(",? ");
        // Checks each word in the name for year format matching
        for (String word : titleInfo) {
            if (word.matches("^\\d{4}$")) {
                return Integer.parseInt(word);
            }
        }
        log.warn("{}: product's crop year is not specified", url);
        return null;
    }

    /**
     * Парсер цены вина.
     * @return Стоимость вина ИЛИ null, если её нет.
     */
    @Override
    public Float parsePrice() {
        try {
            String priceValue = productBlock.selectFirst(".price").ownText();
            priceValue = priceValue.replaceAll(" ", "");
            return Float.parseFloat(priceValue);
        } catch (Exception ex) {
            log.warn("{}: product's price is not specified", url);
            return null;
        }
    }

    /**
     * Парсер рейтинга вина.
     * @return Рейтинг вина ИЛИ null, если его нет.
     */
    @Override
    public Float parseWinestyleRating() {
        try {
            String rating = infoContainer.selectFirst(".info-container meta[itemprop=ratingValue]").attr("content");
            return Float.parseFloat(rating) / 2.f;
        } catch (Exception ex) {
            log.warn("{}: product's winestyle's rating is not specified", url);
            return null;
        }
    }

    /**
     * Парсер объема.
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
            log.warn("{}: product's volume is not specified", url);
            return null;
        }
    }

    /**
     * Парсер производителя вина.
     * @return Производитель ИЛИ null, если его нет.
     */
    @Override
    public String parseManufacturer() {
        try {
            Element manufacturerElement = listDescription.selectFirst("span:contains(Производитель:)");
            Element manufacturerParent = manufacturerElement.parent();
            manufacturerElement.remove();
            String manufacturer = manufacturerParent.text();
            manufacturerParent.remove();
            return manufacturer;
        } catch (NullPointerException ex) {
            log.warn("{}: product's manufacturer is not specified", url);
            return null;
        }
    }

    /**
     * Парсер бренда вина.
     * @return Бренд ИЛИ null, если его нет.
     */
    @Override
    public String parseBrand() {
        try {
            Element brandElement = listDescription.selectFirst("span:contains(Бренд:)");
            Element brandParent = brandElement.parent();
            brandElement.remove();
            String brand = brandParent.text();
            brandParent.remove();
            return brand;
        } catch (NullPointerException ex) {
            log.warn("{}: product's brand is not specified", url);
            return null;
        }
    }

    /**
     * Парсер страны происхождения винограда.
     * @return Страна ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseCountry() {
        try {
            Element countryElement = listDescription.selectFirst("span:contains(Регион:)");
            Element countryParent = countryElement.parent();
            countryElement.remove();
            String countryAndRegion = countryParent.text();
            countryParent.remove();
            int indexOfDelim = countryAndRegion.indexOf(", ");
            if (indexOfDelim >= 0) {
                String country = countryAndRegion.substring(0, indexOfDelim);
                region = countryAndRegion.substring(indexOfDelim + 2);
                return country;
            } else {
                isRegionPresented = false;
                return countryAndRegion;
            }
        } catch (NullPointerException ex) {
            log.warn("{}: product's country and region are not specified", url);
            return null;
        }
    }

    /**
     * Парсер регионов происхождения винограда.
     * @return Регионы ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseRegion() {
        if (isRegionPresented) {
            return region;
        } else {
            log.warn("{}: product's region is not specified", url);
            isRegionPresented = true;
            return null;
        }
    }

    /**
     * Парсер крепости вина.
     * @return Крепость ИЛИ null, если свойства нет.
     */
    @Override
    public Float parseStrength() {
        try {
            Element strengthElement = listDescription.selectFirst("span:contains(Крепость:)");
            Element strengthParent = strengthElement.parent();
            strengthElement.remove();
            String strength = strengthParent.text();
            strengthParent.remove();
            return Float.parseFloat(strength.substring(0, strength.length() - 1));
        } catch (Exception ex) {
            log.warn("{}: product's strength is not specified", url);
            return null;
        }
    }

    /**
     * Парсер сорта винограда.
     * @return Объединенная строка сортов винограда ИЛИ null, если их нет.
     */
    @Override
    public String parseGrape() {
        try {
            Element grapeElement = listDescription.selectFirst("span:contains(Сорт винограда:)");
            Element grapeParent = grapeElement.parent();
            grapeElement.remove();
            String grape = grapeParent.text();
            grapeParent.remove();
            return grape;
        } catch (NullPointerException ex) {
            log.warn("{}: product's grape sort is not specified", url);
            return null;
        }
    }

    /**
     * Парсер свойств: Тип и отеннок вина/игристого.
     * @return Тип напитка ИЛИ массив из двух Null, если свойств нет.
     */
    @Override
    public String parseType(boolean isSpakrling) {
        String type;
        try {
            Element typeAndColorElement = listDescription.selectFirst("span:matches(([Вв]ино)[:/].*)");
            Element typeAndColorParent = typeAndColorElement.parent();
            typeAndColorElement.remove();
            String typeColorSugar = typeAndColorParent.text();
            typeAndColorParent.remove();
            if (isSpakrling) {
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
                        isColorPresented = false;
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
            } else {
                int indexOfDelim = typeColorSugar.indexOf(", ");
                if (indexOfDelim >= 0) {
                    type = typeColorSugar.substring(0, indexOfDelim);
                } else {
                    isSugarPresented = false;
                    type = typeColorSugar;
                }
                if (type.matches("^(?!C|Пол|Р|Б|О|Г|Кра).+")) {
                    colorAndSugar = typeColorSugar.substring(indexOfDelim + 2);
                    isColorPresented = false;
                    return type;
                } else {
                    colorAndSugar = typeColorSugar;
                    return "Вино";
                }
            }
        } catch (NullPointerException e) {
            isColorPresented = false;
            isSugarPresented = false;
            log.warn("{}: product's type, color and sugar are not specified", url);
            return null;
        }
    }

    @Override
    public String parseColor() {
        if (isColorPresented) {
            int indexOfDelim = colorAndSugar.indexOf(", ");
            if (indexOfDelim >= 0) {
                String color = colorAndSugar.substring(0, indexOfDelim);
                colorAndSugar = colorAndSugar.substring(indexOfDelim + 2);
                return color.substring(0, 1).toUpperCase() + color.substring(1);
            } else {
                isSugarPresented = false;
                return colorAndSugar.substring(0, 1).toUpperCase() + colorAndSugar.substring(1);
            }
        } else {
            log.warn("{}: sparkling's color is not specified", url);
            isColorPresented = true;
            return null;
        }
    }

    /**
     * Парсер сладости/сухости.
     * @return Сладость/сухость ИЛИ Null, если свойства нет.
     */
    @Override
    public String parseSugar() {
        if (isSugarPresented) {
            return colorAndSugar;
        } else {
            log.warn("{}: product's sugar is not specified", url);
            isSugarPresented = true;
            return null;
        }
    }

    /**
     * Парсер вкуса вина.
     * @return Вкус вина ИЛИ null, если нет его описания.
     */
    @Override
    public String parseTaste() {
        try {
            Element tasteElement = articlesBlock.selectFirst("span:contains(Вкус)").nextElementSibling();
            return tasteElement.text();
        } catch (NullPointerException ex) {
            log.warn("{}: product's taste is not specified", url);
            return null;
        }
    }

    /**
     * Парсер аромата вина.
     * @return Аромат ИЛИ null, если нет его описания.
     */
    @Override
    public String parseAroma() {
        try {
            Element aromaElement = articlesBlock.selectFirst("span:contains(Аром)").nextElementSibling();
            return aromaElement.text();
        } catch (NullPointerException ex) {
            log.warn("{}: product's aroma is not specified", url);
            return null;
        }
    }

    /**
     * Парсер сочетания вина с блюдами.
     * @return Строку сочетаний ИЛИ null, если их нет.
     */
    @Override
    public String parseFoodPairing() {
        try {
            Element foodPairingElement = articlesBlock.selectFirst("span:contains(Гаст)").nextElementSibling();
            return foodPairingElement.text();
        } catch (NullPointerException ex) {
            log.warn("{}: product's food pairing is not specified", url);
            return null;
        }
    }

    /**
     * Парсер описания.
     * @return Описание, которое мы достали, ИЛИ Null, если описания нет.
     */
    @Override
    public String parseDescription() {
        try {
            Element descriptionElement = descriptionBlock.selectFirst(".description-block");
            return descriptionElement.text();
        } catch (NullPointerException ex) {
            log.warn("{}: product's description is not specified", url);
            return null;
        }
    }
}
