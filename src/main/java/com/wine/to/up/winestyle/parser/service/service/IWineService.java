package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.dto.WineDto;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;

import java.util.List;

public interface IWineService {
    Wine add(WineDto wineDto);
    Wine getWineByName(String name);
    Wine getWineByUrl(String url);
    Wine updatePrice(String price, String url);
    List<Wine> getAllWines();
}
