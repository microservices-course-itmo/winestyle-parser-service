package com.wine.to.up.winestyle.parser.service.service;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface WebPageLoader {
    Document getDocument(String url) throws IOException;
}
