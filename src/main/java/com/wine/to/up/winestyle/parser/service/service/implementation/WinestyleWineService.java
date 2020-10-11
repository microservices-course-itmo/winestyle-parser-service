package com.wine.to.up.winestyle.parser.service.service.implementation;

import com.wine.to.up.winestyle.parser.service.service.WineService;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.repository.WineRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Класс бизнес-логики для работы с вином. Выполняет интерфейс IWineService
 * {@link com.wine.to.up.winestyle.parser.service.service.WineService}
 */
@Service
@AllArgsConstructor
@Slf4j
public class WinestyleWineService implements WineService {
    private final WineRepository wineRepository;

    @Override
    public Wine updatePrice(BigDecimal price, String url){
        Wine wine = getWineByUrl(url);
        wine.setPrice(price);
        return wineRepository.save(wine);
    }

    @Override
    public Wine updateRating(Double rating, String url){
        Wine wine = getWineByUrl(url);
        wine.setRating(rating);
        return wineRepository.save(wine);
    }

    @Override
    public Wine getWineByName(String name){
        return wineRepository.findByName(name);
    }

    @Override
    public List<Wine> getAllWines(){
        return wineRepository.findAll();
    }

    @Override
    public Wine getWineByUrl(String url){
        try {
            return wineRepository.findByUrl(url);
        } catch (InvalidDataAccessResourceUsageException ex) {
            return null;
        }
    }

    @Override
    public Wine add(Wine wine){
        try{
            wineRepository.save(wine);
        } catch(Exception ex){
            log.error("Error on saving wine!!!: {}", wine.toString(), ex);
        }
        return wine;
    }

    @Override
    public Wine getWineByID(long id) throws NoEntityException {
        return wineRepository.findById(id).orElseThrow(() ->
                NoEntityException.createWith(Wine.class.getSimpleName().toLowerCase(), id)
        );
    }
}
