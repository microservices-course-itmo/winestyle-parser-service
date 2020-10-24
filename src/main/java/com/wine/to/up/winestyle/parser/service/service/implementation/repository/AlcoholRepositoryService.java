package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.domain.entity.ErrorOnSaving;
import com.wine.to.up.winestyle.parser.service.repository.AlcoholRepository;

import com.wine.to.up.winestyle.parser.service.repository.ErrorOnSavingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Класс бизнес-логики для работы с напитками.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlcoholRepositoryService {
    private final AlcoholRepository alcoholRepository;
    private final ErrorOnSavingRepository errorOnSavingRepository;

    public void updatePrice(Float price, String url) {
        Alcohol alcohol = getByUrl(url);
        alcohol.setPrice(price);
        alcoholRepository.save(alcohol);
    }

    /**
     * Обновление рейтинга 
     * @param rating новый рейтинг
     * @param url ссылка на напиток, у которого будем обновлять рейтинг
     */
    public void updateRating(Float rating, String url) {
        Alcohol alcohol = getByUrl(url);
        alcohol.setRating(rating);
        alcoholRepository.save(alcohol);
    }

    /**
     * Получение по названию
     * @param name название
     * @return напиток
     */
    public Alcohol getByName(String name) {
        return alcoholRepository.findByName(name);
    }

    /**
     * Получение списка напитков
     * @return список напитков
     */
    public List<Alcohol> getAll() {
        return alcoholRepository.findAll();
    }

    /**
     * Получение всех вин
     * @return список вин
     */
    public List<Alcohol> getAllWines() {
        return alcoholRepository.findAllByType("Вино");
    }

    /**
     * Получение всего шампанского
     * @return список шампанского
     */
    public List<Alcohol> getAllSparkling() {
        return alcoholRepository.findAllByTypeIn(Arrays.asList("Игристое", "Шампанское"));
    }

    /**
     * Получение напитка по ссылке
     * @param url ссылка на напиток
     * @return напиток или NULL, если
     */
    public Alcohol getByUrl(String url) {
        try {
            return alcoholRepository.findByUrl(url);
        } catch (InvalidDataAccessResourceUsageException ex) {
            return null;
        }
    }

    /**
     * Добавление напитка
     * @param alcohol напиток
     */
    public void add(Alcohol alcohol) {
        try {
            alcoholRepository.save(alcohol);
        } catch(Exception ex){
            ErrorOnSaving errorOnSaving = ErrorOnSaving.of(alcohol, new Timestamp(System.currentTimeMillis()), ex.getMessage());
            errorOnSavingRepository.save(errorOnSaving);
            log.error("Error on saving alcohol: {}", alcohol.toString(), ex);
        }
    }

    /**
     * Получение напитка по id
     * @param id номер напитка
     * @return напиток
     * @throws NoEntityException Если нет такого, кидаем эксепшен
     */
    public Alcohol getByID(long id) throws NoEntityException {
        return alcoholRepository.findById(id)
                .orElseThrow(() -> NoEntityException.createWith(Alcohol.class.getSimpleName().toLowerCase(), id));
    }
}
