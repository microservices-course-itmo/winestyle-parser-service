package com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums;

public enum City {
    MSK,
    SPB;

    @Override
    public String toString() {
        switch(this) {
            case MSK: return "Москва";
            case SPB: return "Санкт-Петербург";
            default: throw new IllegalArgumentException();
        }
    }
}
