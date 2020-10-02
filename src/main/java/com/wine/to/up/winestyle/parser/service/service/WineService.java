package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.dto.WineDto;
import com.wine.to.up.winestyle.parser.service.domain.entity.Wine;
import com.wine.to.up.winestyle.parser.service.repository.WineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class WineService implements IWineService {
    private final WineRepository wineRepository;

    public WineService(WineRepository wineRepository){
        this.wineRepository = wineRepository;
    }

    @Override
    public Wine updatePrice(String price, String url){
        Wine wine = getWineByUrl(url);
        wine.setPrice(price);
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
        return wineRepository.findByUrl(url);
    }

    @Override
    public Wine add(WineDto wineDto){
        Wine wine = new Wine();
        wine.setUrl(wineDto.getUrl());
        wine.setImageUrl(wineDto.getImageUrl());
        wine.setTastingNotes(wineDto.getTastingNotes());
        if (wineDto.getName() == null) {
            wine.setName("noName");
        } else
            wine.setName(wineDto.getName());
        if (wineDto.getBrand() == null) {
            wine.setBrand("noBrand");
        } else
            wine.setBrand(wineDto.getBrand());
        if (wineDto.getColor() == null) {
            wine.setColor("noColor");
        } else
            wine.setColor(wineDto.getColor());
        if (wineDto.getRegion() == null) {
            wine.setRegion("noRegion");
        } else
            wine.setRegion(wineDto.getRegion());
        if (wineDto.getVolume() == null) {
            wine.setVolume("noVolume");
        } else
            wine.setVolume(wineDto.getVolume());
        if (wineDto.getStrength() == null) {
            wine.setStrength("noStrength");
        } else
            wine.setStrength(wineDto.getStrength());
        if (wineDto.getSugar() == null) {
            wine.setSugar("noSugar");
        } else
            wine.setSugar(wineDto.getSugar());
        if (wineDto.getPrice() == null) {
            wine.setPrice("noPrice");
        } else
            wine.setPrice(wineDto.getPrice());
        if (wineDto.getGrape() == null) {
            wine.setGrape("noGrape");
        } else
        wine.setGrape(wineDto.getGrape());
        if (wineDto.getYear() == null){
            wine.setYear(0L);
        } else
            wine.setYear(wineDto.getYear());
        if (wineDto.getRating() == null){
            wine.setRating("noRating");
        } else
            wine.setRating(wineDto.getRating());
        try{
            wineRepository.save(wine);
        } catch(Exception ex){
            log.error("Error on saving wine!!!: {}", wine.toString(), ex);
        }

        return wine;
    }
}
