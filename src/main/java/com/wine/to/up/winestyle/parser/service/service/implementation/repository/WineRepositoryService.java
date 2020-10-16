package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.repository.WineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Класс бизнес-логики для работы с вином.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WineRepositoryService {
    private final WineRepository wineRepository;

    public Wine updatePrice(Float price, String url) {
        Wine wine = getByUrl(url);
        wine.setPrice(price);
        return wineRepository.save(wine);
    }

    public Wine updateRating(Double rating, String url) {
        Wine wine = getByUrl(url);
        wine.setRating(rating);
        return wineRepository.save(wine);
    }

    public Wine getByName(String name) {
        return wineRepository.findByName(name);
    }

    public List<Wine> getAll() {
        return wineRepository.findAll();
    }

    public Wine getByUrl(String url) {
        try {
            return wineRepository.findByUrl(url);
        } catch (InvalidDataAccessResourceUsageException ex) {
            return null;
        }
    }

    public Wine add(Wine wine) {
        try{
            wineRepository.save(wine);
        } catch(Exception ex){
            log.error("Error on saving wine!!!: {}", wine.toString(), ex);
        }
        return wine;
    }

    public Wine getByID(long id) throws NoEntityException {
        return wineRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWith(Wine.class.getSimpleName().toLowerCase(), id)
        );
    }
}
