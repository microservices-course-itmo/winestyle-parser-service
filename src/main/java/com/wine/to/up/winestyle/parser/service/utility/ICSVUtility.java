package com.wine.to.up.winestyle.parser.service.utility;

import com.wine.to.up.winestyle.parser.service.service.IWineService;

import java.io.IOException;

public interface ICSVUtility {
    void toCsvFile(IWineService wineService) throws IOException;
}