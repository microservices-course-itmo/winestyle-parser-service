package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import java.time.Duration;
import java.time.LocalDateTime;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.ParserDirectorService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.SegmentationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParserService implements WinestyleParserService {
    private final ParsingService parsingService;
    private final SegmentationService segmentationService;
    private final DocumentService documentService;
    private final RepositoryService alcoholRepositoryService;
    private final ParserDirectorService parserDirectorService;
    private final KafkaMessageSender<UpdateProducts.UpdateProductsMessage> kafkaSendMessageService;
    private final Alcohol.AlcoholBuilder builder = Alcohol.builder();

    @Override
    public void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) throws InterruptedException {
        Document currentDoc = documentService.setAlcoholUrl(mainUrl + relativeUrl).getAlcoholPage();
        LocalDateTime start = LocalDateTime.now();
        int parsed = 0;
        long hoursPassed;
        long minutesPart;
        long secondsPart;

        log.warn("Starting parsing of {}", alcoholType);

        while (currentDoc != null) {
            parsed += runAcrossProducts(mainUrl, currentDoc, alcoholType);
            Duration timePassed = java.time.Duration.between((start), LocalDateTime.now());
            hoursPassed = timePassed.toHours();
            minutesPart = (timePassed.toMinutes() - hoursPassed * 60);
            secondsPart = (timePassed.toSeconds() - minutesPart * 60);
            log.info("Parsing of {}: {} in {} hours {} minutes {} seconds ({} entities per second)",
                    alcoholType, parsed, hoursPassed, minutesPart, secondsPart, parsed / (double) timePassed.toSeconds());
            currentDoc = documentService.getNext();
        }

        log.warn("Finished parsing of {} in {}", alcoholType, java.time.Duration.between((start), LocalDateTime.now()));
    }

    /**
     * Парсер страницы с позициями
     * @param mainUrl     адрес главной страницы сайта
     * @param currentDoc  текущая страница с позициями
     * @param alcoholType тип алкоголя
     * @return количество распаршенных позиций
     * @throws InterruptedException в случае прерывания со стороны пользователя
     */
    private int runAcrossProducts(String mainUrl, Document currentDoc, String alcoholType) throws InterruptedException {
        String productUrl;
        int parsedNow = 0;
        Elements alcohol = segmentationService
                .setMainDocument(currentDoc)
                .setMainMainContent()
                .breakDocumentIntoProductElements();

        for (Element drink : alcohol) {
            parsingService.setProductBlock(segmentationService.setProductBlock(drink).getProductBlock());
            parsingService.setInfoContainer(segmentationService.getInfoContainer());

            productUrl = parsingService.parseUrl();
            log.debug("Now parsing url: {}", productUrl);

            Alcohol result = null;
            try {
                alcoholRepositoryService.getByUrl(productUrl);
                try {
                    alcoholRepositoryService.updatePrice(parsingService.parsePrice(), productUrl);
                    alcoholRepositoryService.updateRating(parsingService.parseWinestyleRating(), productUrl);
                    result = alcoholRepositoryService.getByUrl(productUrl);
                } catch (NoEntityException ignore) { }
            } catch (NoEntityException ex) {
                Document product = documentService.getProduct(mainUrl + productUrl);
                segmentationService.setProductDocument(product).setProductMainContent();
                prepareParsingService();
                parserDirectorService.makeAlcohol(builder, alcoholType);
                result = builder.url(productUrl).build();
                alcoholRepositoryService.add(result);
            }
            assert result != null;
            kafkaSendMessageService.sendMessage(
                    UpdateProducts.UpdateProductsMessage.newBuilder()
                            .setShopLink(mainUrl)
                            .addProducts(result.asProduct())
                            .build()
            );
            parsedNow++;
        }
        return parsedNow;
    }

    private void prepareParsingService() {
        parsingService.setListDescription(segmentationService.getListDescription());
        parsingService.setLeftBlock(segmentationService.getLeftBlock());
        parsingService.setArticlesBlock(segmentationService.getArticlesBlock());
        parsingService.setDescriptionBlock(segmentationService.getDescriptionBlock());
    }
}
