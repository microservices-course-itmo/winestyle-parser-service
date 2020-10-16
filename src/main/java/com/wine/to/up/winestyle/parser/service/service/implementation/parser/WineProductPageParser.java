package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class WineProductPageParser extends ProductPageParser {
    @Override
    public void parseProductPageInfo(Element productPageElement, Wine.WineBuilder builder) {
        // Block containing product's image
        Element leftBlock = productPageElement.selectFirst(".left-aside");

        parseLeftBlock(leftBlock, builder);

        // Block containing product's tasting notes
        Element articleBlock = productPageElement.selectFirst(".articles-col");

        parseArticlesBlock(articleBlock, builder);

        // Block containing product's description
        Element descriptionBlock = productPageElement.selectFirst(".articles-container.desc");

        parseDescriptionBlock(descriptionBlock, builder);
    }

    void parseLeftBlock(Element leftBlock, Wine.WineBuilder builder) {
        String imageUrl;

        imageUrl = parseImageUrl(leftBlock);

        builder.imageUrl(imageUrl);
    }

    void parseArticlesBlock(Element articlesBlock, Wine.WineBuilder builder) {
        String taste;
        String aroma;
        String foodPairing;

        taste = parseTaste(articlesBlock);
        aroma = parseAroma(articlesBlock);
        foodPairing = parseFoodPairing(articlesBlock);

        builder.taste(taste).aroma(aroma).foodPairing(foodPairing);
    }

    void parseDescriptionBlock(Element descriptionBlock, Wine.WineBuilder builder) {
        String description;

        description = parseDescription(descriptionBlock);

        builder.description(description);
    }
}
