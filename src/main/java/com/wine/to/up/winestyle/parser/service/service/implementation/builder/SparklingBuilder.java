package com.wine.to.up.winestyle.parser.service.service.implementation.builder;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.service.MainPageParser;
import com.wine.to.up.winestyle.parser.service.service.ProductPageParser;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.SparklingRepositoryService;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SparklingBuilder {
    private final DocumentService documentService;
    private final SparklingRepositoryService sparklingRepositoryService;
    @Qualifier("sparklingMainPageParser")
    private final MainPageParser sparklingMainPageParser;
    @Qualifier("sparklingProductPageParser")
    private final ProductPageParser sparklingProductPageParser;

    public void parseAndBuild(Element productElement) throws InterruptedException {
        String mainUrl = "https://spb.winestyle.ru";
        String urlToProductPage = productElement.selectFirst("a").attr("href");
        Document productDoc = documentService.getJsoupDocument(mainUrl + urlToProductPage);
        Element productPageElement = productDoc.selectFirst(".main-content");

        Sparkling.SparklingBuilder builder = Sparkling.builder();

        if (sparklingRepositoryService.getByUrl(urlToProductPage) == null) {
            parseMainPageInfo(productElement, builder);
            parseProductPageInfo(productPageElement, builder);
            sparklingRepositoryService.add(builder.url(urlToProductPage).build());
        } else {
            sparklingRepositoryService.updatePrice(sparklingMainPageParser.parsePrice(productElement), urlToProductPage);
            sparklingRepositoryService.updateRating(sparklingMainPageParser.parseWinestyleRating(productElement), urlToProductPage);
        }
    }

    /**
     * Main page parsing
     * @param productElement HTML-блок, содержащий информацию о продукте.
     * @param builder Строитель сущности продукта.
     */
    private void parseMainPageInfo(Element productElement, Sparkling.SparklingBuilder builder) {
        Element infoContainer = productElement.selectFirst(".info-container"); // Product's rest part of information block

        parseHeaderAndRightBlock(productElement, builder);
        parseInfoContainer(infoContainer, builder);
    }

    /**
     * Product page parsing
     * @param productPageElement HTML-документ, содержащий страницу с подробной информацией о продукте.
     * @param builder Строитель сущности продукта.
     */
    private void parseProductPageInfo(Element productPageElement, Sparkling.SparklingBuilder builder) {
        Element leftBlock = productPageElement.selectFirst(".left-aside"); // Product's image block
        Element articleBlock = productPageElement.selectFirst(".articles-col"); // Product's tasting notes block
        Element descriptionBlock = productPageElement.selectFirst(".articles-container.desc"); // Product's description block

        parseLeftBlock(leftBlock, builder);
        parseArticlesBlock(articleBlock, builder);
        parseDescriptionBlock(descriptionBlock, builder);
    }

    /**
     * Product block header and right block parsing
     * @param itemBlock HTML-блок, содержащий информацию о продукте.
     * @param builder Строитель сущности продукта.
     */
    private void parseHeaderAndRightBlock(Element itemBlock, Sparkling.SparklingBuilder builder) {
        String name = sparklingMainPageParser.parseName(itemBlock);
        Float price = sparklingMainPageParser.parsePrice(itemBlock);

        builder.name(name).price(price);
    }

    /**
     * Main page parsing
     * @param infoContainer HTML-блок, содержащий детальное описание продукта.
     * @param builder Строитель сущности продукта.
     */
    private void parseInfoContainer(Element infoContainer, Sparkling.SparklingBuilder builder) {
        Double rating = sparklingMainPageParser.parseWinestyleRating(infoContainer);
        String manufacturer = sparklingMainPageParser.parseManufacturer(infoContainer);
        String brand = sparklingMainPageParser.parseBrand(infoContainer);
        Float volume = sparklingMainPageParser.parseVolume(infoContainer);
        String strength  = sparklingMainPageParser.parseStrength(infoContainer);
        String grape = sparklingMainPageParser.parseGrape(infoContainer);

        String[] countryAndRegions = sparklingMainPageParser.parseCountryAndRegions(infoContainer);
        String country = countryAndRegions[0];
        String region = countryAndRegions[1];

        String[] typeColorSugar = sparklingMainPageParser.parseTypeColorSugar(infoContainer);
        String type = typeColorSugar[0];
        String color = typeColorSugar[1];
        String sugar = typeColorSugar[2];

        builder
                .rating(rating).manufacturer(manufacturer).brand(brand).country(country).region(region)
                .volume(volume).strength(strength).grape(grape).type(type).color(color).sugar(sugar);
    }

    private void parseLeftBlock(Element leftBlock, Sparkling.SparklingBuilder builder) {
        String imageUrl = sparklingProductPageParser.parseImageUrl(leftBlock);

        builder.imageUrl(imageUrl);
    }

    private void parseArticlesBlock(Element articlesBlock, Sparkling.SparklingBuilder builder) {
        String taste = sparklingProductPageParser.parseTaste(articlesBlock);
        String aroma = sparklingProductPageParser.parseAroma(articlesBlock);
        String foodPairing = sparklingProductPageParser.parseFoodPairing(articlesBlock);

        builder.taste(taste).aroma(aroma).foodPairing(foodPairing);
    }

    private void parseDescriptionBlock(Element descriptionBlock, Sparkling.SparklingBuilder builder) {
        String description = sparklingProductPageParser.parseDescription(descriptionBlock);

        builder.description(description);
    }
}
