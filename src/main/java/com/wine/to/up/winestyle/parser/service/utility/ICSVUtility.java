package com.wine.to.up.winestyle.parser.service.utility;

import com.wine.to.up.winestyle.parser.service.repository.WineRepository;

import java.io.IOException;

public interface ICSVUtility {
    void toCsvFile(WineRepository wineRepository) throws IOException;
}