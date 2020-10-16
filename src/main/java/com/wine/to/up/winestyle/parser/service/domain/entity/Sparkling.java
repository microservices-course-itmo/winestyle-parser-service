package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;

/**
 * <pre>
 * Класс - сущность вино, содержащий поля :
 * id - никальный номер,
 * name - название вина,
 * url - ссылка на страницу вина,
 * imageUrl - ссылка на изображение вина,
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
 * type - тип игристого вина,
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
public class Sparkling {
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

    @Column
    private Float price;

    @Column
    private String grape;

    @Column
    private String type;

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
