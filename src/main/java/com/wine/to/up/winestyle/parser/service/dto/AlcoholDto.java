package com.wine.to.up.winestyle.parser.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Передаваемый объект сущности вино. 
 */
@Data
public class AlcoholDto implements Serializable {
    private String name;
    private String type;
    private String url;
    private String imageUrl;
    private Integer cropYear;
    private String manufacturer;
    private String brand;
    private Float price;
    private Float volume;
    private Float rating;
    private String country;
    private String region;
    private String color;
    private String grape;
    private String sugar;
    private String strength;
    private String aroma;
    private String taste;
    private String foodPairing;
    private String description;
}
