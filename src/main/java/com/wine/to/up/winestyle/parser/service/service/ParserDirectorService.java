package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;

public interface ParserDirectorService {
    void makeWine(Wine.WineBuilder builder);
    void makeSparkling(Sparkling.SparklingBuilder builder);
}
