package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;

/**
 * <pre>
 * Класс - сущность вино, содержащий поля :
 * id - никальный номер,
 * name - название вина,
 * type - тип (Вино/Шампанское/Игристое),
 * url - ссылка на страницу вина,
 * imageUrl - ссылка на изображение вина,
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
 * description - описание вина.
 * </pre>
 */
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
