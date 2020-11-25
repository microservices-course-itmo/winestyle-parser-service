package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;

public interface WinestyleParserService {
    void parseBuildSave(String relativeUrl, AlcoholType alcoholType) throws InterruptedException;
}
