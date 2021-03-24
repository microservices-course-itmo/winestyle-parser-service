package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;

public interface WinestyleParserService {
    void parseBuildSave(AlcoholType alcoholType, City city) throws InterruptedException;
}
