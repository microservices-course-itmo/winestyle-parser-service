package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;

public interface ParserDirectorService {
    void makeAlcohol(Alcohol.AlcoholBuilder builder, String alcoholType);
}
