package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ParserDirector implements Director {
    @Getter
    private final ParserApi.Wine.Builder kafkaMessageBuilder = ParserApi.Wine.newBuilder();

    @Override
    public Alcohol makeAlcohol(Parser parser, String mainPageUrl, String productUrl, AlcoholType alcoholType) {
        Alcohol.AlcoholBuilder entityBuilder = Alcohol.builder();

        String name = parser.parseName();
        entityBuilder.name(name);
        kafkaMessageBuilder.setName(name);

        entityBuilder.url(productUrl);
        kafkaMessageBuilder.setLink(productUrl);

        boolean isSparkling = alcoholType == AlcoholType.SPARKLING;
        entityBuilder.type(parser.parseType(isSparkling));
        kafkaMessageBuilder.setSparkling(isSparkling);

        parser.parseImageUrl().ifPresentOrElse(
                value -> {
                    entityBuilder.imageUrl(value);
                    kafkaMessageBuilder.setImage(value);
                },
                () -> entityBuilder.imageUrl(null)
        );

        parser.parseCropYear().ifPresentOrElse(
                value -> {
                    entityBuilder.cropYear(value);
                    kafkaMessageBuilder.setYear(value);
                },
                () -> entityBuilder.cropYear(null)
        );

        parser.parsePrice().ifPresentOrElse(
                value -> {
                    entityBuilder.price(value);
                    kafkaMessageBuilder.setNewPrice(value);
                },
                () -> entityBuilder.price(null)
        );

        parser.parseWinestyleRating().ifPresentOrElse(
                value -> {
                    entityBuilder.rating(value);
                    kafkaMessageBuilder.setRating(value);
                },
                () -> entityBuilder.rating(null)
        );

        parser.parseBrand().ifPresentOrElse(
                value -> {
                    entityBuilder.brand(value);
                    kafkaMessageBuilder.setBrand(value);
                },
                () -> entityBuilder.brand(null)
        );

        parser.parseManufacturer().ifPresentOrElse(
                value -> {
                    entityBuilder.manufacturer(value);
                    kafkaMessageBuilder.setManufacturer(value);
                },
                () -> entityBuilder.manufacturer(null)
        );

        parser.parseVolume().ifPresentOrElse(
                value -> {
                    entityBuilder.volume(value);
                    kafkaMessageBuilder.setCapacity(value);
                },
                () -> entityBuilder.volume(null)
        );

        parser.parseStrength().ifPresentOrElse(
                value -> {
                    entityBuilder.strength(value);
                    kafkaMessageBuilder.setStrength(value);
                },
                () -> entityBuilder.strength(null)
        );

        parser.parseGrape().ifPresentOrElse(
                value -> {
                    entityBuilder.grape(value);
                    kafkaMessageBuilder.addAllGrapeSort(Arrays.asList(value.split(", ")));
                },
                () -> entityBuilder.volume(null)
        );

        parser.parseCountry().ifPresentOrElse(
                value -> {
                    entityBuilder.country(value);
                    kafkaMessageBuilder.setCountry(value);
                },
                () -> entityBuilder.country(null)
        );

        parser.parseRegion().ifPresentOrElse(
                value -> {
                    entityBuilder.region(value);
                    kafkaMessageBuilder.addAllRegion(Arrays.asList(value.split(", ")));
                },
                () -> entityBuilder.region(null)
        );

        parser.parseColor().ifPresentOrElse(
                value -> {
                    entityBuilder.color(value);
                    kafkaMessageBuilder.setColor(matchColorToValue(value));
                },
                () -> entityBuilder.color(null)
        );

        parser.parseSugar().ifPresentOrElse(
                value -> {
                    entityBuilder.sugar(value);
                    kafkaMessageBuilder.setSugar(matchSugarToValue(value));
                },
                () -> entityBuilder.sugar(null)
        );

        parser.parseTaste().ifPresentOrElse(
                value -> {
                    entityBuilder.taste(value);
                    kafkaMessageBuilder.setTaste(value);
                },
                () -> entityBuilder.taste(null)
        );

        parser.parseAroma().ifPresentOrElse(
                value -> {
                    entityBuilder.aroma(value);
                    kafkaMessageBuilder.setFlavor(value);
                },
                () -> entityBuilder.aroma(null)
        );

        parser.parseFoodPairing().ifPresentOrElse(
                value -> {
                    entityBuilder.foodPairing(value);
                    kafkaMessageBuilder.setGastronomy(value);
                },
                () -> entityBuilder.foodPairing(null)
        );

        parser.parseDescription().ifPresentOrElse(
                value -> {
                    entityBuilder.description(value);
                    kafkaMessageBuilder.setDescription(value);
                },
                () -> entityBuilder.description(null)
        );

        return entityBuilder.build();
    }

    public ParserApi.Wine.Builder fillKafkaMessageBuilder(Alcohol source, AlcoholType alcoholType) {
        kafkaMessageBuilder.setName(source.getName());

        kafkaMessageBuilder.setLink(source.getUrl());

        kafkaMessageBuilder.setSparkling(alcoholType == AlcoholType.SPARKLING);

        String imageUrl = source.getImageUrl();
        if (imageUrl != null) {
            kafkaMessageBuilder.setImage(imageUrl);
        }

        Integer cropYear = source.getCropYear();
        if (cropYear != null) {
            kafkaMessageBuilder.setYear(cropYear);
        }

        Float price = source.getPrice();
        if (price != null) {
            kafkaMessageBuilder.setNewPrice(price);
        }

        Float rating = source.getRating();
        if (rating != null) {
            kafkaMessageBuilder.setRating(rating);
        }

        String brand = source.getBrand();
        if (brand != null) {
            kafkaMessageBuilder.setBrand(brand);
        }

        String manufacturer = source.getManufacturer();
        if (manufacturer != null) {
            kafkaMessageBuilder.setManufacturer(manufacturer);
        }

        Float volume = source.getVolume();
        if (volume != null) {
            kafkaMessageBuilder.setCapacity(volume);
        }

        Float strength = source.getStrength();
        if (strength != null) {
            kafkaMessageBuilder.setStrength(strength);
        }

        String grape = source.getGrape();
        if (grape != null) {
            kafkaMessageBuilder.addAllGrapeSort(Arrays.asList(grape.split(", ")));
        }

        String country = source.getCountry();
        if (country != null) {
            kafkaMessageBuilder.setCountry(country);
        }

        String region = source.getRegion();
        if (region != null) {
            kafkaMessageBuilder.addAllRegion(Arrays.asList(region.split(", ")));
        }

        String color = source.getColor();
        if (color != null) {
            kafkaMessageBuilder.setColor(matchColorToValue(color));
        }

        String sugar = source.getSugar();
        if (sugar != null) {
            kafkaMessageBuilder.setSugar(matchSugarToValue(sugar));
        }

        String taste = source.getTaste();
        if (taste != null) {
            kafkaMessageBuilder.setTaste(taste);
        }

        String aroma = source.getAroma();
        if (aroma != null) {
            kafkaMessageBuilder.setFlavor(aroma);
        }

        String foodPairing = source.getFoodPairing();
        if (foodPairing != null) {
            kafkaMessageBuilder.setGastronomy(foodPairing);
        }

        String description = source.getDescription();
        if (description != null) {
            kafkaMessageBuilder.setDescription(description);
        }

        return kafkaMessageBuilder;
    }

    private ParserApi.Wine.Color matchColorToValue(String color) {
        if (color.matches("^(Белое|Голубое)")) {
            return ParserApi.Wine.Color.WHITE;
        } else if (color.startsWith("Розовое")) {
            return ParserApi.Wine.Color.ROSE;
        } else if (color.startsWith("Оранжевое")) {
            return ParserApi.Wine.Color.ORANGE;
        } else if (color.startsWith("Красное")) {
            return ParserApi.Wine.Color.RED;
        } else {
            return ParserApi.Wine.Color.UNRECOGNIZED;
        }
    }

    private ParserApi.Wine.Sugar matchSugarToValue(String sugar) {
        if (sugar.matches("^(Сухое|Брют).*")) {
            return ParserApi.Wine.Sugar.DRY;
        } else if (sugar.startsWith("Полусухое")) {
            return ParserApi.Wine.Sugar.MEDIUM_DRY;
        } else if (sugar.startsWith("Полусладкое")) {
            return ParserApi.Wine.Sugar.MEDIUM;
        } else if (sugar.startsWith("Сладкое")) {
            return ParserApi.Wine.Sugar.SWEET;
        } else {
            return ParserApi.Wine.Sugar.UNRECOGNIZED;
        }
    }
}
