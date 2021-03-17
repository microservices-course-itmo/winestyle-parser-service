package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.domain.entity.Timing;

import java.util.List;

public interface RepositoryService {
    void add(Alcohol alcohol);

    void add(Timing succeedTiming);

    List<Alcohol> getAll();

    List<Alcohol> getAllWines();

    List<Alcohol> getAllSparkling();

    Alcohol getByUrl(String url) throws NoEntityException;

    Alcohol getByID(long id) throws NoEntityException;

    double sinceLastSucceedParsing();
}
