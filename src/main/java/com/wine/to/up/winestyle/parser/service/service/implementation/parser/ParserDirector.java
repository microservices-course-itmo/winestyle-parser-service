package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

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

        Optional.ofNullable(source.getImageUrl()).ifPresent(kafkaMessageBuilder::setImage);

        Optional.ofNullable(source.getCropYear()).ifPresent(kafkaMessageBuilder::setYear);

        Optional.ofNullable(source.getPrice()).ifPresent(kafkaMessageBuilder::setNewPrice);

        Optional.ofNullable(source.getRating()).ifPresent(kafkaMessageBuilder::setRating);

        Optional.ofNullable(source.getBrand()).ifPresent(kafkaMessageBuilder::setBrand);

        Optional.ofNullable(source.getManufacturer()).ifPresent(kafkaMessageBuilder::setManufacturer);

        Optional.ofNullable(source.getVolume()).ifPresent(kafkaMessageBuilder::setCapacity);

        Optional.ofNullable(source.getStrength()).ifPresent(kafkaMessageBuilder::setStrength);

        Optional.ofNullable(source.getGrape()).ifPresent(grape -> kafkaMessageBuilder.addAllGrapeSort(Arrays.asList(grape.split(", "))));

        Optional.ofNullable(source.getCountry()).ifPresent(kafkaMessageBuilder::setCountry);

        Optional.ofNullable(source.getRegion()).ifPresent(region -> kafkaMessageBuilder.addAllRegion(Arrays.asList(region.split(", "))));

        Optional.ofNullable(source.getColor()).ifPresent(color -> kafkaMessageBuilder.setColor(matchColorToValue(color)));

        Optional.ofNullable(source.getSugar()).ifPresent(sugar -> kafkaMessageBuilder.setSugar(matchSugarToValue(sugar)));

        Optional.ofNullable(source.getTaste()).ifPresent(kafkaMessageBuilder::setTaste);

        Optional.ofNullable(source.getAroma()).ifPresent(kafkaMessageBuilder::setFlavor);

        Optional.ofNullable(source.getFoodPairing()).ifPresent(kafkaMessageBuilder::setGastronomy);

        Optional.ofNullable(source.getDescription()).ifPresent(kafkaMessageBuilder::setDescription);

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
