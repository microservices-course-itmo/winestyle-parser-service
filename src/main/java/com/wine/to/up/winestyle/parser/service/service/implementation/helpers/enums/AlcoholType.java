package com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums;

public enum AlcoholType {
    WINE,
    SPARKLING;

    @Override
    public String toString() {
        switch(this) {
            case WINE: return "wine";
            case SPARKLING: return "sparkling";
            default: throw new IllegalArgumentException();
        }
    }
}
