package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;

public interface WinestyleParserService {
    void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) throws InterruptedException;
}
