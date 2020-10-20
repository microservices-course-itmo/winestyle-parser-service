package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;
import lombok.experimental.Accessors;

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
@Accessors(fluent = true)
@Getter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alcohol {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(columnDefinition = "varchar(20)")
    private String type;

    @Column(columnDefinition = "varchar(115)")
    private String name;

    @Column(columnDefinition = "varchar(125)")
    private String url;

    @Column(columnDefinition = "varchar(65)")
    private String imageUrl;

    @OneToOne(mappedBy = "alcohol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Image image;

    @Column
    private Integer cropYear;

    @Column(columnDefinition = "varchar(50)")
    private String manufacturer;

    @Column(columnDefinition = "varchar(50)")
    private String brand;

    @Column(columnDefinition = "varchar(15)")
    private String color;

    @Column(columnDefinition = "varchar(15)")
    private String country;

    @Column(columnDefinition = "varchar(55)")
    private String region;

    @Column
    private Float volume;

    @Column(columnDefinition = "varchar(30)")
    private String strength;

    @Column(columnDefinition = "varchar(50)")
    private String sugar;

    @Setter
    @Column
    private Float price;

    @Column(columnDefinition = "varchar(100)")
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
    private Float rating;
}
