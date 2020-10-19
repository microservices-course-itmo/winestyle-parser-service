package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.repository.AlcoholRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Класс бизнес-логики для работы с вином.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlcoholRepositoryService {
    private final AlcoholRepository alcoholRepository;

    public void updatePrice(Float price, String url) {
        Alcohol alcohol = getByUrl(url);
        alcohol.setPrice(price);
        alcoholRepository.save(alcohol);
    }

    public void updateRating(Double rating, String url) {
        Alcohol alcohol = getByUrl(url);
        alcohol.setRating(rating);
        alcoholRepository.save(alcohol);
    }

    public Alcohol getByName(String name) {
        return alcoholRepository.findByName(name);
    }

    public List<Alcohol> getAll() {
        return alcoholRepository.findAll();
    }

    public List<Alcohol> getAllWines() {
        return alcoholRepository.findAllByType("Вино");
    }

    public List<Alcohol> getAllSparklings() {
        return alcoholRepository.findAllByTypeIn(Arrays.asList("Игристое", "Шампанское"));
    }

    public Alcohol getByUrl(String url) {
        try {
            return alcoholRepository.findByUrl(url);
        } catch (InvalidDataAccessResourceUsageException ex) {
            return null;
        }
    }

    public void add(Alcohol alcohol) {
        try{
            alcoholRepository.save(alcohol);
        } catch(Exception ex){
            log.error("Error on saving alcohol!: {}", alcohol.toString(), ex);
        }
    }

    public Alcohol getByID(long id) throws NoEntityException {
        return alcoholRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWith(Alcohol.class.getSimpleName().toLowerCase(), id)
        );
    }
}
