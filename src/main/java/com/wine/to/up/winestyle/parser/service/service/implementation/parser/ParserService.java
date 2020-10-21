package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;
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
    public void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) throws InterruptedException {
        Document currentDoc = documentService.setAlcoholUrl(mainUrl + relativeUrl).getAlcoholPage();

        while (currentDoc != null) {
            productBlocksRunner(mainUrl, currentDoc, alcoholType);
            currentDoc = documentService.getNext();
        }
    }

    private void productBlocksRunner(String mainUrl, Document currentDoc, String alcoholType) throws InterruptedException {
        String productUrl;
        Elements alcohol = segmentationService
                .setMainDocument(currentDoc)
                .setMainMainContent()
                .breakDocumentIntoProductElements();

        for (Element drink : alcohol) {
            parsingService.setProductBlock(segmentationService.setProductBlock(drink).getProductBlock());
            parsingService.setInfoContainer(segmentationService.getInfoContainer());

            productUrl = parsingService.parseUrl();
            log.info("parsing url: {}", productUrl);

            if (alcoholRepositoryService.getByUrl(productUrl) == null) {
                Document product = documentService.getProduct(mainUrl + productUrl);
                segmentationService.setProductDocument(product).setProductMainContent();
                prepareParsingService();
                parserDirectorService.makeAlcohol(builder, alcoholType);
                Alcohol result = builder.url(productUrl).build();
                alcoholRepositoryService.add(result);
                kafkaSendMessageService.sendMessage(UpdateProducts.UpdateProductsMessage.newBuilder().setShopLink(mainUrl).addProducts(result.asProduct()).build());
            } else {
                alcoholRepositoryService.updatePrice(parsingService.parsePrice(), productUrl);
                alcoholRepositoryService.updateRating(parsingService.parseWinestyleRating(), productUrl);
            }
        }
    }

    private void prepareParsingService() {
        parsingService.setListDescription(segmentationService.getListDescription());
        parsingService.setLeftBlock(segmentationService.getLeftBlock());
        parsingService.setArticlesBlock(segmentationService.getArticlesBlock());
        parsingService.setDescriptionBlock(segmentationService.getDescriptionBlock());
    }
}
