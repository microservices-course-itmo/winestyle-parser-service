package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Класс - сущность "алкоголь".
 */
@Getter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alcohol {

    /**Поле id - уникальный номер*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**Поле type - тип (Вино/Шампанское/Игристое)*/
    @Column(columnDefinition = "varchar(25)")
    @NotNull
    private String type;

    /**Поле name - название напитка*/
    @Column(columnDefinition = "varchar(130)")
    @NotNull
    private String name;

    /**Поле url - ссылка на страницу напитка*/
    @Column(columnDefinition = "varchar(140)")
    @NotNull
    private String url;

    /**Поле imageUrl - ссылка на изображение напитка*/
    @Column(columnDefinition = "varchar(65)")
    private String imageUrl;

    /**Поле image - изображение напитка*/
    @OneToOne(mappedBy = "alcohol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image image;

    /**Поле cropYear - год сбора*/
    @Column
    private Integer cropYear;

    /**Поле manufacturer - производитель*/
    @Column(columnDefinition = "varchar(65)")
    private String manufacturer;

    /**Поле brand - бренд*/
    @Column(columnDefinition = "varchar(50)")
    private String brand;

    /**Поле color - оттенок*/
    @Column(columnDefinition = "varchar(10)")
    private String color;

    /**Поле country - страна происхождения винограда*/
    @Column(columnDefinition = "varchar(15)")
    private String country;

    /**Поле region - регион винограда*/
    @Column(columnDefinition = "varchar(70)")
    private String region;

    /**Поле volume - обьем*/
    @Column
    private Float volume;

    /**Поле strength - крепость*/
    @Column(columnDefinition = "varchar(25)")
    private Float strength;

    /**Поле sugar - сладость/сухость*/
    @Column(columnDefinition = "varchar(20)")
    private String sugar;

    /**Поле price - цена в рублях*/
    @Setter
    @Column
    private Float price;

    /**Поле grape - сорт винограда*/
    @Column(columnDefinition = "varchar(125)")
    private String grape;

    /**Поле taste - вкус*/
    @Column(columnDefinition = "TEXT")
    private String taste;

    /**Поле aroma - аромат*/
    @Column(columnDefinition = "TEXT")
    private String aroma;

    /**Поле foodPairing - сочетания с блюдами*/
    @Column(columnDefinition = "TEXT")
    private String foodPairing;

    /**Поле description - описание напитка*/
    @Column(columnDefinition = "TEXT")
    private String description;

    /**Поле rating - рейтинг*/
    @Setter
    @Column
    private Float rating;
}
