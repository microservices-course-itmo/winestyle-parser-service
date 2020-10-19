package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.service.ParserDirectorService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserDirector implements ParserDirectorService {
    private final ParsingService wineParsing;
    private final ParsingService sparklingParsing;

    public void makeWine(Wine.WineBuilder builder) {
        builder.name(wineParsing.parseName());
        builder.cropYear(wineParsing.parseCropYear());
        builder.price(wineParsing.parsePrice());
        builder.rating(wineParsing.parseWinestyleRating());
        builder.brand(wineParsing.parseBrand());
        builder.manufacturer(wineParsing.parseManufacturer());
        builder.volume(wineParsing.parseVolume());
        builder.strength(wineParsing.parseStrength());
        builder.grape(wineParsing.parseGrape());
        builder.country(wineParsing.parseCountry());
        builder.region(wineParsing.parseRegion());
        builder.color(wineParsing.parseColor());
        builder.sugar(wineParsing.parseSugar());
        builder.imageUrl(wineParsing.parseImageUrl());
        builder.taste(wineParsing.parseTaste());
        builder.aroma(wineParsing.parseAroma());
        builder.foodPairing(wineParsing.parseFoodPairing());
        builder.description(wineParsing.parseDescription());
    }

    public void makeSparkling(Sparkling.SparklingBuilder builder) {
        builder.name(sparklingParsing.parseName());
        builder.cropYear(sparklingParsing.parseCropYear());
        builder.price(sparklingParsing.parsePrice());
        builder.rating(sparklingParsing.parseWinestyleRating());
        builder.brand(sparklingParsing.parseBrand());
        builder.manufacturer(sparklingParsing.parseManufacturer());
        builder.volume(sparklingParsing.parseVolume());
        builder.strength(sparklingParsing.parseStrength());
        builder.grape(sparklingParsing.parseGrape());
        builder.country(sparklingParsing.parseCountry());
        builder.region(sparklingParsing.parseRegion());
        builder.type(sparklingParsing.parseType());
        builder.color(sparklingParsing.parseColor());
        builder.sugar(sparklingParsing.parseSugar());
        builder.imageUrl(sparklingParsing.parseImageUrl());
        builder.taste(sparklingParsing.parseTaste());
        builder.aroma(sparklingParsing.parseAroma());
        builder.foodPairing(sparklingParsing.parseFoodPairing());
        builder.description(sparklingParsing.parseDescription());
    }
}
