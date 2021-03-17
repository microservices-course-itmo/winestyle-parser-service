package com.wine.to.up.winestyle.parser.service.service.implementation.parser.job;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.Scraper;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ApplicationContextLocator;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductBlockSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductPageSegmentor;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.parser.ParserDirector;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductJob {
    @Qualifier("ParserDirector") private final Director director;
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    private final RepositoryService repositoryService;
    private final Scraper scraper;
    private final ProductBlockSegmentor productBlockSegmentor;
    private final ProductPageSegmentor productPageSegmentor;

    private String mainPageUrl;
    private String productUrl;
    private AlcoholType alcoholType;
    private Parser parser;

    public Alcohol getParsedAlcohol(Parser parser, String mainPageUrl, String productUrl, Element productElement, AlcoholType alcoholType) {
        this.mainPageUrl = mainPageUrl;
        this.productUrl = productUrl;
        this.alcoholType = alcoholType;
        this.parser = parser;

        log.info("Now parsing url: {}", productUrl);

        LocalDateTime productParsingStart = LocalDateTime.now();

        Alcohol alcohol;
        ParserApi.WineParsedEvent.Builder kafkaMessageBuilder = ParserApi.WineParsedEvent.newBuilder().setShopLink(mainPageUrl);

        try {
            alcohol = repositoryService.getByUrl(productUrl);

            if (LocalDateTime.now().getDayOfWeek() == DayOfWeek.MONDAY) {
                alcohol = parseProduct(productElement, kafkaMessageBuilder);
            } else {
                alcohol.setPrice(parser.parsePrice().orElse(null));
                alcohol.setRating(parser.parseWinestyleRating().orElse(null));
                repositoryService.add(alcohol);
                kafkaMessageSender.sendMessage(kafkaMessageBuilder.addWines(director.fillKafkaMessageBuilder(alcohol, alcoholType)).build());
            }
        } catch (NoEntityException ex) {
            alcohol = parseProduct(productElement, kafkaMessageBuilder);
        }

        WinestyleParserServiceMetricsCollector.sumDetailsParsingDuration(productParsingStart, LocalDateTime.now());
        WinestyleParserServiceMetricsCollector.incPublished();

        return alcohol;
    }

    private Alcohol parseProduct(Element productElement, ParserApi.WineParsedEvent.Builder kafkaMessageBuilder) {
        Document product = null;

        LocalDateTime detailsFetchingStart = LocalDateTime.now();
        try {
            product = scraper.getJsoupDocument(mainPageUrl + productUrl);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        WinestyleParserServiceMetricsCollector.sumDetailsFetchingDuration(detailsFetchingStart, LocalDateTime.now());

        prepareParsingService(product, productElement);

        Alcohol alcohol = director.makeAlcohol(parser, mainPageUrl, productUrl, alcoholType);

        repositoryService.add(alcohol);

        kafkaMessageSender.sendMessage(kafkaMessageBuilder.addWines(director.getKafkaMessageBuilder()).build());

        return alcohol;
    }

    private void prepareParsingService(Document doc, Element productElement) {

        Element productPageMainContent = productPageSegmentor.extractProductPageMainContent(doc);

        parser.setListDescription(productBlockSegmentor.extractListDescription(productElement));
        parser.setLeftBlock(productPageSegmentor.extractLeftBlock(productPageMainContent));
        parser.setArticlesBlock(productPageSegmentor.extractArticlesBlock(productPageMainContent));
        parser.setDescriptionBlock(productPageSegmentor.extractDescriptionBlock(productPageMainContent));
    }
}