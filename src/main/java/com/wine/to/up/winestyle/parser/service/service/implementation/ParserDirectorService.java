package com.wine.to.up.winestyle.parser.service.service.implementation;

import com.wine.to.up.winestyle.parser.service.service.implementation.builder.SparklingBuilder;
import com.wine.to.up.winestyle.parser.service.service.implementation.builder.WineBuilder;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserDirectorService {
    private final WineBuilder wineBuilder;
    private final SparklingBuilder sparklingBuilder;

    public void parseWine(Element productElement) throws InterruptedException {
        wineBuilder.parseAndBuild(productElement);
    }

    public void parseSparkling(Element productElement) throws InterruptedException {
        sparklingBuilder.parseAndBuild(productElement);
    }
}
