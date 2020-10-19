package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.SegmentationService;
import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.SparklingRepositoryService;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SparklingParserService extends ParserService {
    private final SparklingRepositoryService sparklingRepositoryService;
    private final ParserDirector parserDirectorService;
    private final Sparkling.SparklingBuilder builder = Sparkling.builder();

    public SparklingParserService(DocumentService documentService, SparklingRepositoryService sparklingRepositoryService,
                                  ParserDirector parserDirectorService, SegmentationService segmentationService,
                                  ParsingService sparklingParsing) {
        super(sparklingParsing, segmentationService, documentService);
        this.sparklingRepositoryService = sparklingRepositoryService;
        this.parserDirectorService = parserDirectorService;
    }

    @Override
    void productBlocksRunner(String mainUrl, Document currentDoc) throws InterruptedException {
        String productUrl;
        Elements sparklings = segmentationService
                                                .setMainDocument(currentDoc)
                                                .setMainMainContent()
                                                .breakDocumentIntoProductElements();

        for (Element sparkling : sparklings) {
            parsingService.setProductBlock(segmentationService.setProductBlock(sparkling).getProductBlock());
            parsingService.setInfoContainer(segmentationService.getInfoContainer());

            productUrl = parsingService.parseUrl();
            log.info("parsing url: {}", productUrl);

            if (sparklingRepositoryService.getByUrl(productUrl) == null) {
                segmentationService
                        .setProductDocument(
                                documentService.getProduct(mainUrl + productUrl))
                        .setProductMainContent();
                prepareParser();
                parserDirectorService.makeSparkling(builder);
                sparklingRepositoryService.add(builder.url(productUrl).build());
            } else {
                sparklingRepositoryService.updatePrice(parsingService.parsePrice(), productUrl);
                sparklingRepositoryService.updateRating(parsingService.parseWinestyleRating(), productUrl);
            }
        }
    }
}
