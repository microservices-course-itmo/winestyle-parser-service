package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.ParserDirectorService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserDirector implements ParserDirectorService {
    private final ParsingService alcoholParsing;

    public void makeAlcohol(Alcohol.AlcoholBuilder builder) {
        builder.name(alcoholParsing.parseName());
        builder.cropYear(alcoholParsing.parseCropYear());
        builder.price(alcoholParsing.parsePrice());
        builder.rating(alcoholParsing.parseWinestyleRating());
        builder.brand(alcoholParsing.parseBrand());
        builder.manufacturer(alcoholParsing.parseManufacturer());
        builder.volume(alcoholParsing.parseVolume());
        builder.strength(alcoholParsing.parseStrength());
        builder.grape(alcoholParsing.parseGrape());
        builder.country(alcoholParsing.parseCountry());
        builder.region(alcoholParsing.parseRegion());
        String[] typeAndColor = alcoholParsing.parseTypeAndColor();
        builder.type(typeAndColor[0]);
        builder.color(typeAndColor[1]);
        builder.sugar(alcoholParsing.parseSugar());
        builder.imageUrl(alcoholParsing.parseImageUrl());
        builder.taste(alcoholParsing.parseTaste());
        builder.aroma(alcoholParsing.parseAroma());
        builder.foodPairing(alcoholParsing.parseFoodPairing());
        builder.description(alcoholParsing.parseDescription());
    }
}
