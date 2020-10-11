package com.wine.to.up.winestyle.parser.service.utility;

import com.wine.to.up.winestyle.parser.service.service.WineService;

import java.io.IOException;

public interface CSVUtility {
    void toCsvFile(WineService wineService) throws IOException;
}