package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.SegmentationService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.WineRepositoryService;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WineParserService extends ParserService {
    private final WineRepositoryService wineRepositoryService;
    private final ParserDirector parserDirectorService;
    private final Wine.WineBuilder builder = Wine.builder();

    public WineParserService(DocumentService documentService, WineRepositoryService wineRepositoryService,
                             ParserDirector parserDirectorService, SegmentationService segmentationService,
                             ParsingService wineParsing) {
        super(wineParsing, segmentationService, documentService);
        this.wineRepositoryService = wineRepositoryService;
        this.parserDirectorService = parserDirectorService;
    }

    @Override
    void productBlocksRunner(String mainUrl, Document currentDoc) throws InterruptedException {
        String productUrl;
        Elements wines = segmentationService
                .setMainDocument(currentDoc)
                .setMainMainContent()
                .breakDocumentIntoProductElements();

        for (Element wine : wines) {
            parsingService.setProductBlock(segmentationService.setProductBlock(wine).getProductBlock());
            parsingService.setInfoContainer(segmentationService.getInfoContainer());

            productUrl = parsingService.parseUrl();
            log.info("parsing url: {}", productUrl);

            if (wineRepositoryService.getByUrl(productUrl) == null) {
                segmentationService
                        .setProductDocument(
                                documentService.getProduct(mainUrl + productUrl))
                        .setProductMainContent();
                prepareParser();
                parserDirectorService.makeWine(builder);
                wineRepositoryService.add(builder.url(productUrl).build());
            } else {
                wineRepositoryService.updatePrice(parsingService.parsePrice(), productUrl);
                wineRepositoryService.updateRating(parsingService.parseWinestyleRating(), productUrl);
            }
        }
    }
}
