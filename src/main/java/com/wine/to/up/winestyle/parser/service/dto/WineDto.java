package com.wine.to.up.winestyle.parser.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Передаваемый объект сущности вино. 
 */
@Data
public class WineDto implements Serializable {
    private static final long serialVersionUID = -6729161659875598272L;
    
    private String url;
    private String imageUrl;
    private String name;
    private Integer cropYear;
    private String manufacturer;
    private String brand;
    private String color;
    private String country;
    private String region;
    private Double volume;
    private String strength;
    private String sugar;
    private BigDecimal price;
    private String grape;
    private String tastingNotes;
    private Double rating;
    private String description;
}
