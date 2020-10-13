package com.wine.to.up.winestyle.parser.service.service;

import org.jsoup.nodes.Document;

public interface DocumentService {
    Document getJsoupDocument(String url) throws InterruptedException;

    Integer pagesNumber(Document doc);
}
