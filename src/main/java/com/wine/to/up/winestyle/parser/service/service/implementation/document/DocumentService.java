package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    private final ScrapingService scrapingService;
    private int pagesNumber;
    private int nextPageNumber = 1;

    @Accessors(chain = true)
    @Setter
    private String alcoholUrl;

    public Document getAlcoholPage() throws InterruptedException {
        Document mainDoc = scrapingService.getJsoupDocument(alcoholUrl);
        pagesNumber = pagesNumber(mainDoc);
        log.info("Parsing: {}", alcoholUrl);
        return mainDoc;
    }

    public Document getNext() throws InterruptedException {
        nextPageNumber++;
        if(nextPageNumber <= pagesNumber) {
            log.info("Parsing: {}?page={}", alcoholUrl, nextPageNumber);
            return scrapingService.getJsoupDocument(alcoholUrl + "?page=" + nextPageNumber);
        } else {
            return null;
        }
    }

    public Document getProduct(String productUrl) throws InterruptedException {
        return scrapingService.getJsoupDocument(productUrl);
    }

    /**
     * @return количество страниц данного документа.
     */
    private int pagesNumber(Document mainDoc) {
        try {
            String pagesNumber = mainDoc.selectFirst("#CatalogPagingBottom li:last-of-type").text();
            return Integer.parseInt(pagesNumber);
        } catch (NullPointerException ex) {
            return 1;
        }
    }
}
