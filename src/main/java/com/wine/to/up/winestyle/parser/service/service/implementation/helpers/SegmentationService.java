package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SegmentationService {
    @Accessors(chain = true)
    @Setter
    private Document mainDocument;
    @Accessors(chain = true)
    @Setter
    private Document productDocument;
    @Accessors(chain = true)
    @Setter
    @Getter
    private Element productBlock;
    private Element mainMainContent;
    private Element productMainContent;
    private Element infoContainer;

    public SegmentationService setMainMainContent() {
        mainMainContent = mainDocument.selectFirst(".main-content"); // Product's block inner element
        return this;
    }

    public void setProductMainContent() {
        productMainContent = productDocument.selectFirst(".main-content"); // Product's page inner element
    }

    public Elements breakDocumentIntoProductElements() {
        return mainMainContent.getElementsByClass("item-block");
    }

    public Element getInfoContainer() {
        infoContainer = productBlock.selectFirst(".info-container"); // Product's rest part of information block
        return infoContainer;
    }

    public Element getListDescription() {
        return infoContainer.selectFirst(".list-description");
    }

    public Element getLeftBlock() {
        return productMainContent.selectFirst(".left-aside"); // Product's image block
    }

    public Element getArticlesBlock() {
        return productMainContent.selectFirst(".articles-col"); // Product's tasting notes block
    }

    public Element getDescriptionBlock() {
        return productMainContent.selectFirst(".articles-container.desc"); // Product's description block
    }
}
