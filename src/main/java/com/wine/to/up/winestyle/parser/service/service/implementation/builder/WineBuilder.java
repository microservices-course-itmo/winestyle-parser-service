package com.wine.to.up.winestyle.parser.service.service.implementation.builder;

import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.service.MainPageParser;
import com.wine.to.up.winestyle.parser.service.service.ProductPageParser;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.WineRepositoryService;

import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineBuilder {
    private final DocumentService documentService;
    private final WineRepositoryService wineRepositoryService;
    @Qualifier("wineMainPageParser")
    private final MainPageParser wineMainPageParser;
    @Qualifier("wineProductPageParser")
    private final ProductPageParser wineProductPageParser;

    public void parseAndBuild(Element productElement) throws InterruptedException {
        String mainUrl = "https://spb.winestyle.ru";
        String urlToProductPage = productElement.selectFirst("a").attr("href");
        Document productDoc = documentService.getJsoupDocument(mainUrl + urlToProductPage);
        Element productPageElement = productDoc.selectFirst(".main-content");

        Wine.WineBuilder builder = Wine.builder();

        if (wineRepositoryService.getByUrl(urlToProductPage) == null) {
            parseMainPageInfo(productElement, builder);
            parseProductPageInfo(productPageElement, builder);
            wineRepositoryService.add(builder.url(urlToProductPage).build());
        } else {
            wineRepositoryService.updatePrice(wineMainPageParser.parsePrice(productElement), urlToProductPage);
            wineRepositoryService.updateRating(wineMainPageParser.parseWinestyleRating(productElement), urlToProductPage);
        }
    }

    /**
     * Main page parsing
     * @param productElement HTML-блок, содержащий информацию о продукте.
     * @param builder Строитель сущности продукта.
     */
    private void parseMainPageInfo(Element productElement, Wine.WineBuilder builder) {
        Element infoContainer = productElement.selectFirst(".info-container"); // Product's rest part of information block

        parseHeaderAndRightBlock(productElement, builder);
        parseInfoContainer(infoContainer, builder);
    }

    /**
     * Product page parsing
     * @param productPageElement HTML-документ, содержащий страницу с подробной информацией о продукте.
     * @param builder Строитель сущности продукта.
     */
    private void parseProductPageInfo(Element productPageElement, Wine.WineBuilder builder) {
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
    private void parseHeaderAndRightBlock(Element itemBlock, Wine.WineBuilder builder) {
        String name;
        Integer cropYear;
        Float price;

        name = wineMainPageParser.parseName(itemBlock);
        cropYear = wineMainPageParser.parseCropYear(name);
        price = wineMainPageParser.parsePrice(itemBlock);

        builder.name(name).cropYear(cropYear).price(price);
    }

    /**
     * Main page parsing
     * @param infoContainer HTML-блок, содержащий детальное описание продукта.
     * @param builder Строитель сущности продукта.
     */
    private void parseInfoContainer(Element infoContainer, Wine.WineBuilder builder) {
        Double rating = wineMainPageParser.parseWinestyleRating(infoContainer);
        String manufacturer = wineMainPageParser.parseManufacturer(infoContainer);
        String brand = wineMainPageParser.parseBrand(infoContainer);

        Float volume = wineMainPageParser.parseVolume(infoContainer);
        String strength = wineMainPageParser.parseStrength(infoContainer);
        String grape = wineMainPageParser.parseGrape(infoContainer);

        String[] countryAndRegions = wineMainPageParser.parseCountryAndRegions(infoContainer);
        String country = countryAndRegions[0];
        String region = countryAndRegions[1];

        String[] colorAndSugar = wineMainPageParser.parseColorAndSugar(infoContainer);
        String color = colorAndSugar[0];
        String sugar = colorAndSugar[1];

        builder
                .rating(rating).manufacturer(manufacturer).brand(brand).country(country).region(region)
                .volume(volume).strength(strength).grape(grape).color(color).sugar(sugar);
    }

    private void parseLeftBlock(Element leftBlock, Wine.WineBuilder builder) {
        String imageUrl = wineProductPageParser.parseImageUrl(leftBlock);

        builder.imageUrl(imageUrl);
    }

    private void parseArticlesBlock(Element articlesBlock, Wine.WineBuilder builder) {
        String taste = wineProductPageParser.parseTaste(articlesBlock);
        String aroma = wineProductPageParser.parseAroma(articlesBlock);
        String foodPairing = wineProductPageParser.parseFoodPairing(articlesBlock);

        builder.taste(taste).aroma(aroma).foodPairing(foodPairing);
    }

    private void parseDescriptionBlock(Element descriptionBlock, Wine.WineBuilder builder) {
        String description = wineProductPageParser.parseDescription(descriptionBlock);

        builder.description(description);
    }
}
