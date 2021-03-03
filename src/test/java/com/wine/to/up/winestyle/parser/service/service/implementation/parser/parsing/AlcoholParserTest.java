package com.wine.to.up.winestyle.parser.service.service.implementation.parser.parsing;

import com.wine.to.up.winestyle.parser.service.service.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AlcoholParserTest {
    private Parser alcoholParser;

    String MAIN_CONTENT_HTML = "<div class=\"main-content main-content-filters\">" +
									"<form class=\"item-block \">" +
										"<div class=\"item-block-content\">" +
											"<div class=\"item-header \">" +
												"<p class=\"title\" data-prodname=\"testname, 1990\"></p>" +
												"<div class=\"meta\">" +
													"<a href=\"/products/test.html\" class=\"text\">testname, 2000, 750 мл</a>" +
												"</div>"+
											"</div>" +
											"<div class=\"right-block\">" +
												"<div class=\"left-tablet\">" +
													"<div class=\"price-container\">" +
														"<div class=\"price \">123<span>руб.</span>" + 
														"</div>" +
													"</div>" +
												"/div>" +
											"</div>" +
											"<div class=\"info-container\">" +
                                            "<label><span class=\"volume-block__title\">Объем</span>0.75 л</label>" +
                                                "<span itemprop=\"aggregateRating\">" +
                                                    "<meta itemprop=\"ratingValue\" content=\"8.0\">" +
                                                "</span>" +
												"<div class=\"info-block item-review\" role=\"article\">" + 
													"<span class=\"text\">" +
														"<span class=\"wine-reviews\">" + 
															"test_review" +
														"</span>" +
													"</span>" +
												"</div>" +
												"<ul class=\"list-description\">" +
													"<li>" +
														"<span class=\"name\">" + "Вино:" + "</span>" +
														"<a>Красное</a>, <a>Сухое</a>" +
													"</li>" +
                                                    "<li>" +
                                                    "<span class=\"name\">Регион:</span>" +
                                                    "<div class=\"links\">" +
                                                        "<a>Франция</a>, <a>Шампань</a>" +
                                                    "</div>" +
                                                    "</li>" +
                                                    "<li>" +
                                                        "<span class=\"name\">Производитель:</span>" +
                                                        "<div class=\"links\">" +
                                                            "<a href=\"/wine/test_manufacturer/\">test</a>" +
                                                        "</div>" +
                                                    "</li>" +
                                                    "<meta itemprop=\"manufacturer\" content=\"test_manufacturer\">" +
                                                    "<meta itemprop=\"brand\" content=\"test_brand\">" +
                                                    "<meta itemprop=\"releaseDate\" content=\"2020\">" +
                                                    "<li>" +
                                                        "<span class=\"name\">Бренд:</span>"+
                                                        "<div class=\"links\">" +
                                                            "<a>test_brand</a>" +
                                                        "</div>" +
                                                    "</li>" +
                                                    "<li>" +
                                                        "<span class=\"name\">Крепость:</span>" +
                                                        "<div class=\"links\">" +
                                                            "<span><a href=\"/wine/13alc/\">20%</a></span>" +
                                                        "</div>" +
                                                    "</li>" +
                                                    "<li>" +
                                                        "<span class=\"name\">Объем:</span>" +
                                                        "<div class=\"links\">" +
                                                            "<a href=\"/wine/750ml/\">750 мл</a>" +
                                                        "</div>" +
                                                    "</li>" +
                                                    "<li>" +
                                                        "<span class=\"name\">Сорт винограда:</span>" + //"Виноград:"?
                                                        "<div class=\"links\">" +
                                                            "<a href=\"/wine/merlot/\">Мерло</a>: 100%" +
                                                        "</div>" +
                                                    "</li>" +
												"</ul>" +
											"</div>" +
										"</div>" +
									"</form>" +
                                    "<div class=\"item-content\">" +
                                        "<div class=\"left-aside left-aside_no-bg\">" +
                                            "<a itempromp=\"image\" class=\"img-container fancybox \" href=\"test-imgurl.jpg\">" +
                                            "</a>" +
                                        "</div>" +
                                        "<div class=\"right-aside\">" +
                                            "<div class=\"articles-container articles-col\">" +
                                                "<div class=\"description-block\"" +
                                                    "<span class=\"title\">Цвет:</span>" +
                                                    "<p>Цветное.</p>" +
                                                "</div>" +
                                                "<div class=\"description-block\"" +
                                                    "<span class=\"title\">Вкус:</span>" +
	                                                "<p itemprop=\"description\">Вкусное.</p>" +
                                                "</div>" +
                                                "<div class=\"description-block\"" +
                                                    "<span class=\"title\">Аромат:</span>" +
                                                    "<p>Ароматное.</p>" +
                                                "</div>" +
                                                "<div class=\"description-block\"" +
                                                    "<span class=\"title\">Гастрономические сочетания:</span>" +
                                                    "<p>Сочетается.</p>" +
                                                "</div>" +
                                            "</div>" +
                                            "<div class=\"articles-container collapsible-block desc opened-half\">" +
		                                        "<h2 winestyle-collapse=\"next-all\" collapse-mobile-sm=\"true\" class=\"collapse-title \">Интересные факты</h2>" +
		                                        "<div class=\"description-block collapse-content collapse-content-processed\" style=\"display: block;\">" +
                                                    "<p>" +
                                                    "Описание." +
                                                    "</p>" +
                                                "</div>" +
	                                        "</div>" +
                                        "</div>" +
                                    "</div>" +
								"</div>";

    @BeforeEach
    void setUp() {
        Document doc = Jsoup.parse(MAIN_CONTENT_HTML);
        alcoholParser = new AlcoholParser();
        Element productMainContent = doc.selectFirst(".main-content");
        Element infoContainer = productMainContent.selectFirst(".info-container");
        Element listDescription = infoContainer.selectFirst(".list-description");
        Element leftBlock = productMainContent.selectFirst(".left-aside");
        Element articlesBlock = productMainContent.selectFirst(".articles-col");
        Element descriptionBlock = productMainContent.selectFirst(".articles-container.desc");

        alcoholParser.setProductBlock(productMainContent);
        alcoholParser.setListDescription(listDescription);
        alcoholParser.setLeftBlock(leftBlock);
        alcoholParser.setInfoContainer(infoContainer);
        alcoholParser.setDescriptionBlock(descriptionBlock);
        alcoholParser.setArticlesBlock(articlesBlock);

        ReflectionTestUtils.setField(alcoholParser, "nameElementCssQuery", ".title");
        ReflectionTestUtils.setField(alcoholParser, "urlElementCssQuery", "a");
        ReflectionTestUtils.setField(alcoholParser, "imageUrlElementCssQuery", "a.img-container");
        ReflectionTestUtils.setField(alcoholParser, "priceElementCssQuery", ".price");
        ReflectionTestUtils.setField(alcoholParser, "winestyleRatingElementCssQuery", ".info-container meta[itemprop=ratingValue]");
        ReflectionTestUtils.setField(alcoholParser, "volumeElementCssQuery", "label");
        ReflectionTestUtils.setField(alcoholParser, "manufacturerElementCssQuery", "span:contains(Производитель:)");
        ReflectionTestUtils.setField(alcoholParser, "brandElementCssQuery", "span:contains(Бренд:)");
        ReflectionTestUtils.setField(alcoholParser, "countryElementCssQuery", "span:contains(Регион:)");
        ReflectionTestUtils.setField(alcoholParser, "strengthElementCssQuery", "span:contains(Крепость:)");
        ReflectionTestUtils.setField(alcoholParser, "grapeElementCssQuery", "span:contains(Сорт винограда:)");
        ReflectionTestUtils.setField(alcoholParser, "typeElementCssQuery", "span:matches(([Вв]ино)[:/].*)");
        ReflectionTestUtils.setField(alcoholParser, "tasteElementCssQuery", "span:contains(Вкус:)");
        ReflectionTestUtils.setField(alcoholParser, "aromaElementCssQuery", "span:contains(Аромат:)");
        ReflectionTestUtils.setField(alcoholParser, "foodPairingCssQuery", "span:contains(Гастрономические сочетания:)");
        ReflectionTestUtils.setField(alcoholParser, "descriptionCssQuery", ".description-block");
        ReflectionTestUtils.setField(alcoholParser, "namePropertyCssAttr", "data-prodname");
        ReflectionTestUtils.setField(alcoholParser, "imageUrlPropertyCssAttr", "href");
        ReflectionTestUtils.setField(alcoholParser, "winestyleRatingPropertyCssAttr", "content");
    }

    @Test
    void parseName() {
        String name = alcoholParser.parseName();
        assertEquals("testname, 1990", name);
    }

    @Test
    void parseUrl() {
        String url = alcoholParser.parseUrl();
        assertEquals("/products/test.html", url);
    }

    @Test //TODO: all types alcohol with color and sugar
    void parseType() {
        String type = alcoholParser.parseType(false);
        assertEquals("Вино", type);
    }

    @Test
    void parseImageUrl() {
        Optional<String> imageUrl = alcoholParser.parseImageUrl();
        assertEquals("test-imgurl.jpg", imageUrl.get());
    }

    @Test
    void parseCropYear() {
        alcoholParser.parseName();
        Optional<Integer> cropYear = alcoholParser.parseCropYear();
        assertEquals(1990, cropYear.get());
    }

    @Test
    void parsePrice() {
        Optional<Float> price = alcoholParser.parsePrice();
        assertEquals(123F, price.get());
    }

    @Test
    void parseWinestyleRating() {
        Optional<Float> rating = alcoholParser.parseWinestyleRating();
        assertEquals(4F, rating.get());
    }

    @Test
    void parseVolume() {
        Optional<Float> volume = alcoholParser.parseVolume();
        assertEquals(0.75F, volume.get());
    }

    @Test
    void parseManufacturer() {
        Optional<String> manufacturer = alcoholParser.parseManufacturer();
        assertEquals("test", manufacturer.get());
    }

    @Test
    void parseBrand() {
        Optional<String> brand = alcoholParser.parseBrand();
        assertEquals("test_brand", brand.get());
    }

    @Test
    void parseCountry() {
        Optional<String> country = alcoholParser.parseCountry();
        assertEquals("Франция", country.get());
    }

    @Test
    void parseRegion() {
        alcoholParser.parseCountry();
        Optional<String> region = alcoholParser.parseRegion();
        assertEquals("Шампань", region.get());
    }

    @Test
    void parseStrength() {
        Optional<Float> strength = alcoholParser.parseStrength();
        assertEquals(20F, strength.get());
    }

    @Test
    void parseGrape() {
        Optional<String> grape = alcoholParser.parseGrape();
        assertEquals("Мерло: 100%", grape.get());
    }

    @Test //TODO: sparkling
    void parseColor() {
        alcoholParser.parseType(false);
        Optional<String> color = alcoholParser.parseColor();
        assertEquals("Красное", color.get());
    }

    @Test //TODO: sparkling
    void parseSugar() {
        alcoholParser.parseType(false);
        alcoholParser.parseColor();
        Optional<String> sugar = alcoholParser.parseSugar();
        assertEquals("Сухое", sugar.get());
    }

    @Test
    void parseTaste() {
        Optional<String> taste = alcoholParser.parseTaste();
        assertEquals("Вкусное.", taste.get());
    }

    @Test
    void parseAroma() {
        Optional<String> aroma = alcoholParser.parseAroma();
        assertEquals("Ароматное.", aroma.get());
    }

    @Test
    void parseFoodPairing() {
        Optional<String> foodPairing = alcoholParser.parseFoodPairing();
        assertEquals("Сочетается.", foodPairing.get());
    }

    @Test
    void parseDescription() {
        Optional<String> description = alcoholParser.parseDescription();
        assertEquals("Описание.", description.get());
    }

    @Test
    void setProductBlock() {
        String stringElement = "<html>test</html>";
        Element expectedElement = Jsoup.parse(stringElement).getAllElements().first();
        alcoholParser.setProductBlock(expectedElement);
        assertEquals(expectedElement, ReflectionTestUtils.getField(alcoholParser, "productBlock"));
    }

    @Test
    void setInfoContainer() {
        String stringElement = "<html>test</html>";
        Element expectedElement = Jsoup.parse(stringElement).getAllElements().first();
        alcoholParser.setInfoContainer(expectedElement);
        assertEquals(expectedElement, ReflectionTestUtils.getField(alcoholParser, "infoContainer"));
    }

    @Test
    void setListDescription() {
        String stringElement = "<html>test</html>";
        Element expectedElement = Jsoup.parse(stringElement).getAllElements().first();
        alcoholParser.setListDescription(expectedElement);
        assertEquals(expectedElement, ReflectionTestUtils.getField(alcoholParser, "listDescription"));;
    }

    @Test
    void setLeftBlock() {
        String stringElement = "<html>test</html>";
        Element expectedElement = Jsoup.parse(stringElement).getAllElements().first();
        alcoholParser.setLeftBlock(expectedElement);
        assertEquals(expectedElement, ReflectionTestUtils.getField(alcoholParser, "leftBlock"));
    }

    @Test
    void setArticlesBlock() {
        String stringElement = "<html>test</html>";
        Element expectedElement = Jsoup.parse(stringElement).getAllElements().first();
        alcoholParser.setArticlesBlock(expectedElement);
        assertEquals(expectedElement, ReflectionTestUtils.getField(alcoholParser, "articlesBlock"));
    }

    @Test
    void setDescriptionBlock() {
        String stringElement = "<html>test</html>";
        Element expectedElement = Jsoup.parse(stringElement).getAllElements().first();
        alcoholParser.setDescriptionBlock(expectedElement);
        assertEquals(expectedElement, ReflectionTestUtils.getField(alcoholParser, "descriptionBlock"));
    }

    @Test
    void Sparkling() {
        String LIST_DESCRIPTION_CONTENT = "<ul class=\"list-description\">" +
                                                "<li>" +
                                                    "<span class=\"name\">Игристое вино/шампанское:</span>" +
                                                    "<a>Игристое-белое</a>, <a>Сухое</a>, <a>Брют</a>" +
                                                "</li>" +
                                            "</ul>";
        Document doc = Jsoup.parse(LIST_DESCRIPTION_CONTENT);
        Element listDescription = doc.selectFirst(".list-description");
        alcoholParser.setListDescription(listDescription);
        alcoholParser.parseType(true);
        Optional<String> color = alcoholParser.parseColor();
        Optional<String> sugar = alcoholParser.parseSugar();
        assertEquals("Белое", color.get());
        assertEquals("Сухое, Брют", sugar.get());

    }

    @Test
    void parseEachWithNull() {
        String EMPTY_CONTENT = "<div class=\"main-content\">" +
                                    "<div class=\"info-container\">" +
                                        "<div class=\"list-description\"></div>" +
                                    "</div>" +
                                    "<div class=\"left-aside\"></div>" +
                                    "<div class=\"articles-col\"></div>" +
                                    "<div class=\"articles-container desc\"></div>" +
                                "</div>";
        Document doc = Jsoup.parse(EMPTY_CONTENT);
        Element productMainContent = doc.selectFirst(".main-content");
        Element infoContainer = productMainContent.selectFirst(".info-container");
        Element listDescription = infoContainer.selectFirst(".list-description");
        Element leftBlock = productMainContent.selectFirst(".left-aside");
        Element articlesBlock = productMainContent.selectFirst(".articles-col");
        Element descriptionBlock = productMainContent.selectFirst(".articles-container.desc");
        alcoholParser.setProductBlock(productMainContent);
        alcoholParser.setListDescription(listDescription);
        alcoholParser.setLeftBlock(leftBlock);
        alcoholParser.setInfoContainer(infoContainer);
        alcoholParser.setDescriptionBlock(descriptionBlock);
        alcoholParser.setArticlesBlock(articlesBlock);

        assertEquals(Optional.empty(), alcoholParser.parseCountry());
        assertEquals(Optional.empty(), alcoholParser.parseAroma());
        assertEquals(Optional.empty(), alcoholParser.parseBrand());
        assertEquals(Optional.empty(), alcoholParser.parseDescription());
        assertEquals(Optional.empty(), alcoholParser.parseFoodPairing());
        assertEquals(Optional.empty(), alcoholParser.parseGrape());
        assertEquals(Optional.empty(), alcoholParser.parseImageUrl());
        assertEquals(Optional.empty(), alcoholParser.parseManufacturer());
        assertEquals(Optional.empty(), alcoholParser.parsePrice());
        assertEquals(Optional.empty(), alcoholParser.parseAroma());
        assertEquals(Optional.empty(), alcoholParser.parseStrength());
        assertEquals(Optional.empty(), alcoholParser.parseTaste());
        assertEquals(Optional.empty(), alcoholParser.parseVolume());
        assertEquals(Optional.empty(), alcoholParser.parseWinestyleRating());

    }
}