package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;

import java.util.List;

public interface RepositoryService {
    <T> T updatePrice(Float price, String url);
    <T> T updateRating(Double rating, String url);
    <T> T getByUrl(String url);
    <T extends Sparkling> T add(T sparkling);
    <T extends Wine> T add(T wine);
    <T> T getByID(long id) throws NoEntityException;
    <T> List<T> getAll();
}
