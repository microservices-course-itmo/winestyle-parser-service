package com.wine.to.up.winestyle.parser.service.service.implementation.parser.job;

import com.wine.to.up.winestyle.parser.service.service.Parser;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.ProductBlockSegmentor;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductUrlJob {
    private final ProductBlockSegmentor productBlockSegmentor;

    public String get(Parser parser, Element productElement) {
        parser.setProductBlock(productElement);
        parser.setInfoContainer(productBlockSegmentor.extractInfoContainer(productElement));
        return parser.parseUrl();
    }
}