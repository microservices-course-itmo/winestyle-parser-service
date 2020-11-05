package com.wine.to.up.winestyle.parser.service.service.implementation.document;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface IWebPageLoader {
    Document getDocument(String url) throws IOException;
}
