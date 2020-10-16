package com.wine.to.up.winestyle.parser.service.service;

import org.jsoup.nodes.Element;

public interface ProductPageParser {
    String parseImageUrl(Element el);
    String parseTaste(Element el);
    String parseAroma(Element el);
    String parseFoodPairing(Element el);
    String parseDescription(Element el);
}
