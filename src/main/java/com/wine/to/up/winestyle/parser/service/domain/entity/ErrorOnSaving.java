package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Класс - сущность ошибки при сохранении алкоголя.
 */
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorOnSaving {
    
    /**Поле id - уникальный номер*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**Поле type - тип (Вино/Шампанское/Игристое)*/
    @Column(columnDefinition = "TEXT")
    private String type;

    /**Поле name - название напитка*/
    @Column(columnDefinition = "TEXT")
    private String name;

    /**Поле url - ссылка на страницу напитка*/
    @Column(columnDefinition = "TEXT")
    private String url;

    /**Поле imageUrl - ссылка на изображение напитка*/
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    /**Поле cropYear - год сбора*/
    @Column(columnDefinition = "TEXT")
    private String cropYear;

    /**Поле manufacturer - производитель*/
    @Column(columnDefinition = "TEXT")
    private String manufacturer;

    /**Поле brand - бренд*/
    @Column(columnDefinition = "TEXT")
    private String brand;

    /**Поле color - оттенок*/
    @Column(columnDefinition = "TEXT")
    private String color;

    /**Поле country - страна происхождения винограда*/
    @Column(columnDefinition = "TEXT")
    private String country;

    /**Поле region - регион винограда*/
    @Column(columnDefinition = "TEXT")
    private String region;

    /**Поле volume - обьем*/
    @Column(columnDefinition = "TEXT")
    private String volume;

    /**Поле strength - крепость*/
    @Column(columnDefinition = "TEXT")
    private Float strength;

    /**Поле isugar - сладость/сухость*/
    @Column(columnDefinition = "TEXT")
    private String sugar;

    /**Поле price - цена в рублях*/
    @Column(columnDefinition = "TEXT")
    private String price;

    /**Поле grape - сорт винограда*/
    @Column(columnDefinition = "TEXT")
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
    @Column(columnDefinition = "TEXT")
    private String rating;

    /**Поле unsavedId - id при попытки сохранения*/
    @Column
    private Long unsavedId;

    /**Поле timestamp - время попытки сохранения*/
    @Column
    private Timestamp timestamp;

    /**Поле error - текст ошибки при сохранении*/
    @Column(columnDefinition = "TEXT")
    private String error;

    /**
     * Создание сущности ошибки.
     * @param alcohol - сущность Алкоголя, при которой произошла ошибка.
     * @param timestamp - время ошибки.
     * @param error - описание ошибки.
     * @return ошибка при сохранении алкоголя.
     */
    public static ErrorOnSaving of(Alcohol alcohol, Timestamp timestamp, String error) {
        return ErrorOnSaving.builder()
                .name(alcohol.getName())
                .type(alcohol.getType())
                .url(alcohol.getUrl())
                .imageUrl(alcohol.getImageUrl())
                .cropYear(alcohol.getCropYear() == null ? null : alcohol.getCropYear().toString())
                .manufacturer(alcohol.getManufacturer())
                .brand(alcohol.getBrand())
                .color(alcohol.getColor())
                .country(alcohol.getCountry())
                .region(alcohol.getRegion())
                .volume(alcohol.getVolume() == null ? null : alcohol.getVolume().toString())
                .strength(alcohol.getStrength())
                .sugar(alcohol.getSugar())
                .price(alcohol.getPrice() == null ? null : alcohol.getPrice().toString())
                .grape(alcohol.getGrape())
                .taste(alcohol.getTaste())
                .aroma(alcohol.getAroma())
                .foodPairing(alcohol.getFoodPairing())
                .description(alcohol.getDescription())
                .rating(alcohol.getRating() == null ? null : alcohol.getRating().toString())
                .unsavedId(alcohol.getId())
                .error(error)
                .timestamp(timestamp)
                .build();
    }
}
