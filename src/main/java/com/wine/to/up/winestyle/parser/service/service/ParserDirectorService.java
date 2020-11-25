package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;

public interface ParserDirectorService {
    Alcohol makeAlcohol(String productUrl, AlcoholType alcoholType);
}
