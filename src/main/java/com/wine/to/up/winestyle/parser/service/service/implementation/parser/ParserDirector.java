package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.ParserDirectorService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserDirector implements ParserDirectorService {
    private final ParsingService alcoholParsing;

    @Override
    public Alcohol makeAlcohol(String productUrl, AlcoholType alcoholType) {
        return Alcohol.builder()
                .type(alcoholParsing.parseType(alcoholType != AlcoholType.WINE))
                .name(alcoholParsing.parseName())
                .url(productUrl)
                .cropYear(alcoholParsing.parseCropYear())
                .price(alcoholParsing.parsePrice())
                .rating(alcoholParsing.parseWinestyleRating())
                .brand(alcoholParsing.parseBrand())
                .manufacturer(alcoholParsing.parseManufacturer())
                .volume(alcoholParsing.parseVolume())
                .strength(alcoholParsing.parseStrength())
                .grape(alcoholParsing.parseGrape())
                .country(alcoholParsing.parseCountry())
                .region(alcoholParsing.parseRegion())
                .color(alcoholParsing.parseColor())
                .sugar(alcoholParsing.parseSugar())
                .imageUrl(alcoholParsing.parseImageUrl())
                .taste(alcoholParsing.parseTaste())
                .aroma(alcoholParsing.parseAroma())
                .foodPairing(alcoholParsing.parseFoodPairing())
                .description(alcoholParsing.parseDescription())
                .build();
    }
}
