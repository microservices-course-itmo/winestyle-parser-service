package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * <pre>
 * Класс - сущность "алкоголь", содержащая поля:
 * id - никальный номер,
 * name - название напитка,
 * type - тип (Вино/Шампанское/Игристое),
 * url - ссылка на страницу напитка,
 * imageUrl - ссылка на изображение напитка,
 * image - изображение напитка,
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
 * description - описание напитка.
 * </pre>
 */
@Getter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alcohol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "varchar(25)")
    @NotNull
    private String type;

    @Column(columnDefinition = "varchar(130)")
    @NotNull
    private String name;

    @Column(columnDefinition = "varchar(140)")
    @NotNull
    private String url;

    @Column(columnDefinition = "varchar(65)")
    private String imageUrl;

    @OneToOne(mappedBy = "alcohol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image image;

    @Column
    private Integer cropYear;

    @Column(columnDefinition = "varchar(65)")
    private String manufacturer;

    @Column(columnDefinition = "varchar(50)")
    private String brand;

    @Column(columnDefinition = "varchar(10)")
    private String color;

    @Column(columnDefinition = "varchar(15)")
    private String country;

    @Column(columnDefinition = "varchar(70)")
    private String region;

    @Column
    private Float volume;

    @Column(columnDefinition = "varchar(25)")
    private Float strength;

    @Column(columnDefinition = "varchar(20)")
    private String sugar;

    @Setter
    @Column
    private Float price;

    @Column(columnDefinition = "varchar(125)")
    private String grape;

    @Column(columnDefinition = "TEXT")
    private String taste;

    @Column(columnDefinition = "TEXT")
    private String aroma;

    @Column(columnDefinition = "TEXT")
    private String foodPairing;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Setter
    @Column
    private Float rating;
}
