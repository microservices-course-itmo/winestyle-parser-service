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

    @Column
    private String type;

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

    @Column(columnDefinition = "TEXT")
    private String taste;

    @Column(columnDefinition = "TEXT")
    private String aroma;

    @Column(columnDefinition = "TEXT")
    private String foodPairing;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Float rating;

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
                .cropYear(alcohol.getCropYear())
                .manufacturer(alcohol.getManufacturer())
                .brand(alcohol.getBrand())
                .color(alcohol.getColor())
                .country(alcohol.getCountry())
                .region(alcohol.getRegion())
                .volume(alcohol.getVolume())
                .strength(alcohol.getStrength())
                .sugar(alcohol.getSugar())
                .price(alcohol.getPrice())
                .grape(alcohol.getGrape())
                .taste(alcohol.getTaste())
                .aroma(alcohol.getAroma())
                .foodPairing(alcohol.getFoodPairing())
                .description(alcohol.getDescription())
                .rating(alcohol.getRating())
                .unsavedId(alcohol.getId())
                .error(error)
                .timestamp(timestamp)
                .build();
    }
}
