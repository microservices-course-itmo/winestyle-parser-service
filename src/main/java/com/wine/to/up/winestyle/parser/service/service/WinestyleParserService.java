package com.wine.to.up.winestyle.parser.service.service;

public interface WinestyleParserService {
    void parseBuildSave(String mainUrl, String relativeUrl, String alcoholType) throws InterruptedException;
}
