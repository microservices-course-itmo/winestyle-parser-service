package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * <pre>
 * Класс - сущность вино, содержащий поля :
 * id - никальный номер, 
 * name - название вина, 
 * url - ссылка на страницу вина,
 * imageUrl - ссылка на изображение вина,
 * cropYear - год сбора,
 * manufacturer - производитель,
 * brand - бренд,
 * color - оттенок,
 * contry - страна,
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
 * description - описание вина.
 * </pre>
 */
@Setter
@Getter
@ToString
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

    @Column(columnDefinition="TEXT")
    private String aroma;

    @Column(columnDefinition="TEXT")
    private String foodPairing;

    @Column(columnDefinition="TEXT")
    private String description;

    @Column
    private Double rating;
}
