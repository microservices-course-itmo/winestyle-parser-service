package com.wine.to.up.winestyle.parser.service.service.implementation;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;

import com.wine.to.up.winestyle.parser.service.service.implementation.parser.SparklingMainPageParser;
import com.wine.to.up.winestyle.parser.service.service.implementation.parser.SparklingProductPageParser;
import com.wine.to.up.winestyle.parser.service.service.implementation.parser.WineMainPageParser;
import com.wine.to.up.winestyle.parser.service.service.implementation.parser.WineProductPageParser;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.SparklingRepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.WineRepositoryService;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParserDirectorService {
    private final WineMainPageParser wineMainPageParser;
    private final WineProductPageParser wineProductPageParser;
    private final WineRepositoryService wineRepositoryService;
    private final SparklingRepositoryService sparklingRepositoryService;

    private final SparklingMainPageParser sparklingMainPageParser;
    private final SparklingProductPageParser sparklingProductPageParser;

    public void parseWine(Element productElement, Element productPageElement, Wine.WineBuilder builder) {
        wineMainPageParser.parseMainPageInfo(productElement, builder);
        wineProductPageParser.parseProductPageInfo(productPageElement, builder);
    }

    public void parseSparkling(Element productElement, Element productPageElement, Sparkling.SparklingBuilder builder) {
        sparklingMainPageParser.parseMainPageInfo(productElement, builder);
        sparklingProductPageParser.parseProductPageInfo(productPageElement, builder);
    }

    /**
     * Update potentially modified data
     *
     * @param el          Контейнер, в котором лежит цена и рейтинг продукта.
     * @param url         Строка-ссылка на страницу продукта.
     * @param alcoholType Тип алкоголя для вызова методов соответствующего репозиторий-сервиса.
     */
    public void updatePriceAndRating(Element el, String url, String alcoholType) {
        switch (alcoholType) {
            case "wine":
                wineRepositoryService.updatePrice(wineMainPageParser.parsePrice(el), url);
                wineRepositoryService.updateRating(wineMainPageParser.parseWinestyleRating(el), url);
                break;
            case "sparkling":
                sparklingRepositoryService.updatePrice(sparklingMainPageParser.parsePrice(el), url);
                sparklingRepositoryService.updateRating(sparklingMainPageParser.parseWinestyleRating(el), url);
                break;
        }
    }
}
