package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.domain.entity.ErrorOnSaving;
import com.wine.to.up.winestyle.parser.service.domain.entity.Timing;
import com.wine.to.up.winestyle.parser.service.repository.AlcoholRepository;
import com.wine.to.up.winestyle.parser.service.repository.ErrorOnSavingRepository;
import com.wine.to.up.winestyle.parser.service.repository.TimingRepository;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Класс бизнес-логики для работы с напитками.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationRepositoryService implements RepositoryService {
    private final AlcoholRepository alcoholRepository;
    private final ErrorOnSavingRepository errorOnSavingRepository;
    private final TimingRepository timingRepository;

    @Override
    public void updatePrice(Float price, String url) throws NoEntityException {
        Alcohol alcohol = getByUrl(url);
        alcohol.setPrice(price);
        alcoholRepository.save(alcohol);
    }

    /**
     * Обновление рейтинга
     *
     * @param rating новый рейтинг
     * @param url    ссылка на напиток, у которого будем обновлять рейтинг
     * @throws NoEntityException при отсутствии сущности
     */
    @Override
    public void updateRating(Float rating, String url) throws NoEntityException {
        Alcohol alcohol = getByUrl(url);
        alcohol.setRating(rating);
        alcoholRepository.save(alcohol);
    }


    /**
     * Получение списка напитков
     *
     * @return список напитков
     */
    @Override
    public List<Alcohol> getAll() {
        return alcoholRepository.findAll();
    }

    /**
     * Получение всех вин
     *
     * @return список вин
     */
    @Override
    public List<Alcohol> getAllWines() {
        return alcoholRepository.findAllWines();
    }

    /**
     * Получение всего шампанского
     *
     * @return список шампанского
     */
    @Override
    public List<Alcohol> getAllSparkling() {
        return alcoholRepository.findAllSparkling();
    }

    /**
     * Получение напитка по ссылке
     *
     * @param url ссылка на напиток
     * @return напиток или NULL, если
     * @throws NoEntityException при отсутствии сущности
     */
    @Override
    public Alcohol getByUrl(String url) throws NoEntityException {
        return alcoholRepository.findByUrl(url).orElseThrow(() ->
                NoEntityException.createWith(Alcohol.class.getSimpleName().toLowerCase(), null, url)
        );
    }

    /**
     * Добавление напитка
     *
     * @param alcohol напиток
     */
    @Override
    public void add(Alcohol alcohol) {
        try {
            alcoholRepository.save(alcohol);
        } catch (Exception ex) {
            ErrorOnSaving errorOnSaving = ErrorOnSaving.of(
                    alcohol,
                    new Timestamp(System.currentTimeMillis()),
                    Arrays.toString(ex.getStackTrace())
            );
            log.error("Error on saving alcohol: {}", alcohol.toString(), ex);
            errorOnSavingRepository.save(errorOnSaving);
        }
    }

    /**
     * Получение напитка по id
     *
     * @param id номер напитка
     * @return напиток
     * @throws NoEntityException Если нет такого, кидаем эксепшен
     */
    @Override
    public Alcohol getByID(long id) throws NoEntityException {
        return alcoholRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWith(Alcohol.class.getSimpleName().toLowerCase(), id, null)
        );
    }

    @Override
    public double sinceLastSucceedParsing() {
        Timing lastSucceedDate = timingRepository.findFirstByOrderByIdDesc();
        if(lastSucceedDate == null) {
            return 0;
        } else {
            return Duration.between(lastSucceedDate.getParsingSucceedDate(), LocalDateTime.now()).toNanos() / 1e9d;
        }
    }

    @Override
    public void add(Timing succeedTiming) {
        timingRepository.save(succeedTiming);
    }
}
