package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * <pre>
 * Класс - сущность ошибки при сохранении алкоголя, содержащий поля:
 * id - никальный номер,
 * name - название напитка,
 * type - тип (Вино/Шампанское/Игристое),
 * url - ссылка на страницу напитка,
 * imageUrl - ссылка на изображение напитка,
 * manufacturer - производитель,
 * brand - бренд,
 * color - оттенок,
 * country - страна происхождения винограда,
 * region - регион винограда,
 * volume - обьем,
 * strength - крепость,
 * sugar - сладость/сухость,
 * price - цена в рублях,
 * grape - сорт винограда,
 * taste - вкус,
 * aroma - аромат,
 * foodPairing - сочетания с блюдами,
 * rating - рейтинг,
 * description - описание напитка,
 * unsavedId - id при попытки сохранения,
 * timestamp - время попытки сохранения,
 * error - текст ошибки при сохранении.
 * </pre>
 */
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorOnSaving {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String type;

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String cropYear;

    @Column(columnDefinition = "TEXT")
    private String manufacturer;

    @Column(columnDefinition = "TEXT")
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String color;

    @Column(columnDefinition = "TEXT")
    private String country;

    @Column(columnDefinition = "TEXT")
    private String region;

    @Column(columnDefinition = "TEXT")
    private String volume;

    @Column(columnDefinition = "TEXT")
    private String strength;

    @Column(columnDefinition = "TEXT")
    private String sugar;

    @Column(columnDefinition = "TEXT")
    private String price;

    @Column(columnDefinition = "TEXT")
    private String grape;

    @Column(columnDefinition = "TEXT")
    private String taste;

    @Column(columnDefinition = "TEXT")
    private String aroma;

    @Column(columnDefinition = "TEXT")
    private String foodPairing;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String rating;

    @Column
    private Long unsavedId;

    @Column
    private Timestamp timestamp;

    @Column(columnDefinition = "TEXT")
    private String error;

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
