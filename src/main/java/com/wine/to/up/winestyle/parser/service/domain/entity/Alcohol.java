package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;

@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alcohol {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String type;

    @Column
    private String name;

    @Column
    private String url;

    @Column
    private String imageUrl;

    @OneToOne(mappedBy = "alcohol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image image;

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
    private Float volume;

    @Column
    private String strength;

    @Column
    private String sugar;

    @Setter
    @Column
    private Float price;

    @Column
    private String grape;

    @Column(columnDefinition="TEXT")
    private String taste;

    @Column(columnDefinition="TEXT")
    private String aroma;

    @Column(columnDefinition="TEXT")
    private String foodPairing;

    @Column(columnDefinition="TEXT")
    private String description;

    @Setter
    @Column
    private Double rating;
}
