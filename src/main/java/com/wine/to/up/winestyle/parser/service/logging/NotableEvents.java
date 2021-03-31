package com.wine.to.up.winestyle.parser.service.logging;


import com.wine.to.up.commonlib.logging.NotableEvent;

public enum NotableEvents implements NotableEvent {
    I_WINES_PAGE_PARSED("Wine page parsed: {}"),
    I_WINE_DETAILS_PARSED("Wine ({}) parsed details: {}"),
    W_WINE_PAGE_PARSING_FAILED("Failed to parse {} page"),
    W_WINE_DETAILS_PARSING_FAILED("Failed to parse {} page");

    private final String template;

    NotableEvents(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public String getName() {
        return name();
    }
}
