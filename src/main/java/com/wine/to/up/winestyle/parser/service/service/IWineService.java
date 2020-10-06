package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;

import java.math.BigDecimal;
import java.util.List;

public interface IWineService {
    Wine add(Wine wine);
    Wine getWineByName(String name);
    Wine getWineByUrl(String url);
    Wine updatePrice(BigDecimal price, String url);
    Wine updateRating(Double price, String url);
    List<Wine> getAllWines();
}
