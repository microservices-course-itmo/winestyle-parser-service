package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import java.time.Duration;
import java.time.LocalDateTime;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.ParserDirectorService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.SegmentationService;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.AlcoholRepositoryService;
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
    private final AlcoholRepositoryService alcoholRepositoryService;
    private final ParserDirectorService parserDirectorService;
    private final KafkaMessageSender<UpdateProducts.UpdateProductsMessage> kafkaSendMessageService;
    private final Alcohol.AlcoholBuilder builder = Alcohol.builder();

    @Override
    public void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) throws InterruptedException, NoEntityException {
        Document currentDoc = documentService.setAlcoholUrl(mainUrl + relativeUrl).getAlcoholPage();
        LocalDateTime start = LocalDateTime.now();
        int parsed = 0;

        log.warn("Starting parsing of {}", alcoholType);

        while (currentDoc != null) {
            parsed += productBlocksRunner(mainUrl, currentDoc, alcoholType);
            Duration timePassed = java.time.Duration.between(LocalDateTime.now(), (start));
            log.info("Parsing of {}: {} in {} minutes ({} entities per second)", alcoholType, parsed, timePassed.toMinutes(), parsed / (double) timePassed.toSeconds());
            currentDoc = documentService.getNext();
        }

        log.warn("Finished parsing of {} in {}", alcoholType, java.time.Duration.between(LocalDateTime.now(), (start)));
    }

    /**
     * Парсер страницы с позициями
     * @param mainUrl адрес главной страницы сайта
     * @param currentDoc текущая страница с позициями
     * @param alcoholType тип алкоголя
     * @return количество распаршенных позиций
     * @throws InterruptedException в случае прерывания со стороны пользователя
     */
    private int productBlocksRunner(String mainUrl, Document currentDoc, String alcoholType) throws InterruptedException, NoEntityException {
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

            Alcohol result;
            if (alcoholRepositoryService.getByUrl(productUrl) == null) {
                Document product = documentService.getProduct(mainUrl + productUrl);
                segmentationService.setProductDocument(product).setProductMainContent();
                prepareParsingService();
                parserDirectorService.makeAlcohol(builder, alcoholType);
                result = builder.url(productUrl).build();
                alcoholRepositoryService.add(result);
            } else {
                alcoholRepositoryService.updatePrice(parsingService.parsePrice(), productUrl);
                alcoholRepositoryService.updateRating(parsingService.parseWinestyleRating(), productUrl);
                result = alcoholRepositoryService.getByUrl(productUrl);
            }
            kafkaSendMessageService.sendMessage(UpdateProducts.UpdateProductsMessage.newBuilder().setShopLink(mainUrl).addProducts(result.asProduct()).build());
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
