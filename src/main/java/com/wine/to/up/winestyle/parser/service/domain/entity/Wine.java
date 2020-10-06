package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String url;

    @Column
    private String imageUrl;

    @Column
    private Integer cropYear;

    @Column
    private String manufacturer;

    @Column
    private String brand;

    @Column
    private String color;

    @Column
    private String country;

    @Column
    private String region;

    @Column
    private Double volume;

    @Column
    private String strength;

    @Column
    private String sugar;

    @Column
    private BigDecimal price;

    @Column
    private String grape;

    @Column(columnDefinition="TEXT")
    private String taste;

    @Column
    private String aroma;

    @Column(columnDefinition="TEXT")
    private String foodPairing;

    @Column
    private Double rating;

    @Column(columnDefinition="TEXT")
    private String description;

    @Override
    public String toString(){
        return url + " "
                + name + " "
                + color + " "
                + grape + " "
                + brand + " "
                + region + " "
                + volume + " "
                + strength + " "
                + sugar + " "
                + price + " "
                + rating + " "
                + cropYear + " "
                + description;
    }
}
