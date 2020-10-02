package com.wine.to.up.winestyle.parser.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WineDto implements Serializable {
    private String url;
    private String imageUrl;
    private String name;
    private Long year;
    private String brand;
    private String color;
    private String region;
    private String volume;
    private String strength;
    private String sugar;
    private String price;
    private String grape;
    private String tastingNotes;
    private String rating;
}
