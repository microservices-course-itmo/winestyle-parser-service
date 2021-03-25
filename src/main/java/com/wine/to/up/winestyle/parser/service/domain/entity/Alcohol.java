package com.wine.to.up.winestyle.parser.service.domain.entity;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.City;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    /**
     * Поле id - уникальный номер
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Поле type - тип (Вино/Шампанское/Игристое)
     */
    @Column(columnDefinition = "VARCHAR(25)")
    @NotNull
    private String type;

    /**
     * Поле name - название напитка
     */
    @Column(columnDefinition = "VARCHAR(130)")
    @NotNull
    private String name;

    /**
     * Поле url - ссылка на страницу напитка
     */
    @Column(columnDefinition = "VARCHAR(140)")
    @NotNull
    private String url;

    /**
     * Поле city - enum с указанием города, откуда была спаршена позиция
     */
    @Column(columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private City city;

    /**
     * Поле imageUrl - ссылка на изображение напитка
     */
    @Column(columnDefinition = "VARCHAR(65)")
    private String imageUrl;

    /**
     * Поле image - изображение напитка
     */
    @OneToOne(mappedBy = "alcohol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image image;

    /**
     * Поле cropYear - год сбора
     */
    @Column
    private Integer cropYear;

    /**
     * Поле manufacturer - производитель
     */
    @Column(columnDefinition = "VARCHAR(65)")
    private String manufacturer;

    /**
     * Поле brand - бренд
     */
    @Column(columnDefinition = "VARCHAR(50)")
    private String brand;

    /**
     * Поле color - оттенок
     */
    @Column(columnDefinition = "VARCHAR(10)")
    private String color;

    /**
     * Поле country - страна происхождения винограда
     */
    @Column(columnDefinition = "VARCHAR(15)")
    private String country;

    /**
     * Поле region - регион винограда
     */
    @Column(columnDefinition = "VARCHAR(70)")
    private String region;

    /**
     * Поле volume - обьем
     */
    @Column
    private Float volume;

    /**
     * Поле strength - крепость
     */
    @Column(columnDefinition = "VARCHAR(25)")
    private Float strength;

    /**
     * Поле sugar - сладость/сухость
     */
    @Column(columnDefinition = "VARCHAR(20)")
    private String sugar;

    /**
     * Поле price - цена в рублях
     */
    @Setter
    @Column
    private Float price;

    /**
     * Поле grape - сорт винограда
     */
    @Column(columnDefinition = "VARCHAR(125)")
    private String grape;

    /**
     * Поле availability - наличие в продаже
     */
    @Column(columnDefinition = "BOOL")
    private Boolean availability;

    /**
     * Поле taste - вкус
     */
    @Column(columnDefinition = "TEXT")
    private String taste;

    /**
     * Поле aroma - аромат
     */
    @Column(columnDefinition = "TEXT")
    private String aroma;

    /**
     * Поле foodPairing - сочетания с блюдами
     */
    @Column(columnDefinition = "TEXT")
    private String foodPairing;

    /**
     * Поле description - описание напитка
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Поле rating - рейтинг
     */
    @Setter
    @Column
    private Float rating;

    @NotNull
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime dateAdded;
}
