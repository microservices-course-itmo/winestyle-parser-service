package com.wine.to.up.winestyle.parser.service.service.implementation.parser;

import com.wine.to.up.winestyle.parser.service.service.ParsingService;
import com.wine.to.up.winestyle.parser.service.service.WinestyleParserService;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.DocumentService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.SegmentationService;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;

@RequiredArgsConstructor
public abstract class ParserService implements WinestyleParserService {
    final ParsingService parsingService;
    final SegmentationService segmentationService;
    final DocumentService documentService;

    @Override
    public void parseBuildSave(String mainUrl, String relativeUrl) throws InterruptedException {
        Document currentDoc = documentService.setAlcoholUrl(mainUrl + relativeUrl).getAlcoholPage();

        while (currentDoc != null) {
            productBlocksRunner(mainUrl, currentDoc);
            currentDoc = documentService.getNext();
        }
    }

    void prepareParser() {
        parsingService.setLeftBlock(segmentationService.getLeftBlock());
        parsingService.setArticlesBlock(segmentationService.getArticlesBlock());
        parsingService.setDescriptionBlock(segmentationService.getDescriptionBlock());
    }

    abstract void productBlocksRunner(String mainUrl, Document currentDoc) throws InterruptedException;
}
