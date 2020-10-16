package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Sparkling;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.repository.SparklingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Класс бизнес-логики для работы с игристым вином.
 */
@Service
@RequiredArgsConstructor
@Qualifier("sparklingRepositoryService")
@Slf4j
public class SparklingRepositoryService implements RepositoryService {
    private final SparklingRepository sparklingRepository;

    @Override
    public Sparkling updatePrice(Float price, String url) {
        Sparkling sparkling = getByUrl(url);
        sparkling.setPrice(price);
        return sparklingRepository.save(sparkling);
    }

    @Override
    public Sparkling updateRating(Double rating, String url) {
        Sparkling sparkling = getByUrl(url);
        sparkling.setRating(rating);
        return sparklingRepository.save(sparkling);
    }

    @Override
    public List<Sparkling> getAll() {
        return sparklingRepository.findAll();
    }

    @Override
    public Sparkling getByUrl(String url) {
        try {
            return sparklingRepository.findByUrl(url);
        } catch (InvalidDataAccessResourceUsageException ex) {
            return null;
        }
    }

    @Override
    public Sparkling add(Sparkling sparkling) {
        try {
            sparklingRepository.save(sparkling);
        } catch (Exception ex) {
            log.error("Error on saving wine!: {}", sparkling.toString(), ex);
        }
        return sparkling;
    }

    @Override
    public Sparkling getByID(long id) throws NoEntityException {
        return sparklingRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWith(Sparkling.class.getSimpleName().toLowerCase(), id)
        );
    }

    @Override
    public <T extends Wine> T add(T wine) {
        throw new UnsupportedOperationException();
    }
}
