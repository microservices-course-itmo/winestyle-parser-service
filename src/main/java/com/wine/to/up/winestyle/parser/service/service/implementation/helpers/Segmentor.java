package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Класс, отвечающий за разделение данных со страницы
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Segmentor {
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

    @Value("${spring.jsoup.segmenting.css.query.main-main}")
    private String mainMainElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.product-main}")
    private String productMainElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.info}")
    private String infoElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.list-description}")
    private String listDescriptionElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.block-description}")
    private String blockDescriptionElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.left-block}")
    private String leftBlockElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.query.articles-block}")
    private String articlesBlockElementCssQuery;
    @Value("${spring.jsoup.segmenting.css.class.product}")
    private String productElementClassName;

    public void setProductMainContent() {
        productMainContent = productDocument.selectFirst(".main-content");
    }

    /**
     * Разделение на элементы
     *
     * @return
     */
    public Elements breakDocumentIntoProductElements() {
        return mainMainContent.getElementsByClass("item-block");
    }

    /**
     * Получить остальную информацию
     *
     * @return контейнер с информацией
     */
    public Element getInfoContainer() {
        infoContainer = productBlock.selectFirst(infoElementCssQuery);
        return infoContainer;
    }

    /**
     * Описания
     *
     * @return из контейнера берется список описаний
     */
    public Element getListDescription() {
        return infoContainer.selectFirst(listDescriptionElementCssQuery);
    }

    /**
     * Взятие блока с картинкой
     *
     * @return блок с картинкой
     */
    public Element getLeftBlock() {
        return productMainContent.selectFirst(leftBlockElementCssQuery);
    }

    /**
     * Описание вкуса
     *
     * @return блок с описанием
     */
    public Element getArticlesBlock() {
        return productMainContent.selectFirst(articlesBlockElementCssQuery);
    }

    /**
     * Описание продукта
     *
     * @return описание
     */
    public Element getDescriptionBlock() {
        return productMainContent.selectFirst(blockDescriptionElementCssQuery);
    }
}
