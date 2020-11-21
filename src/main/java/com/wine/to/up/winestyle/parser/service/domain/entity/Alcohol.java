package com.wine.to.up.winestyle.parser.service.domain.entity;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.parser.common.api.schema.ParserApi.Wine.Color;
import com.wine.to.up.parser.common.api.schema.ParserApi.Wine.Sugar;
import lombok.*;

import javax.persistence.*;
import java.util.Arrays;

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
    private String type;

    @Column(columnDefinition = "varchar(130)")
    private String name;

    @Column(columnDefinition = "varchar(140)")
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

    /**
     * Преобразование нашего класса Wine в общий для парсеров класс Product
     *
     * @return Product
     */
    public ParserApi.Wine asProduct() {
        ParserApi.Wine.Builder builder = ParserApi.Wine.newBuilder();

        builder.setName(name);

        builder.setLink(url);

        if (imageUrl != null) {
            builder.setImage(imageUrl);
        }

        if (cropYear != null) {
            builder.setYear(cropYear);
        }

        if (manufacturer != null) {
            builder.setManufacturer(manufacturer);
        }

        if (brand != null) {
            builder.setBrand(brand);
        }

        if (color != null) {
            builder.setColor(matchColorToValue(color));
        }

        if (country != null) {
            builder.setCountry(country);
        }

        if (region != null) {
            builder.addAllRegion(Arrays.asList(region.split(",")));
        }

        if (volume != null) {
            builder.setCapacity(volume);
        }

        if (strength != null) {
            builder.setStrength(strength);
        }

        if (sugar != null) {
            builder.setSugar(matchSugarToValue(sugar));
        }

        if (price != null) {
            builder.setNewPrice(price);
        }

        if (grape != null) {
            Iterable<String> grapeIterable = Arrays.asList(grape.split(","));
            builder.addAllGrapeSort(grapeIterable);
        }

        if (taste != null) {
            builder.setTaste(taste);
        }

        if (aroma != null) {
            builder.setFlavor(aroma);
        }

        if (foodPairing != null) {
            builder.setGastronomy(foodPairing);
        }

        if (description != null) {
            builder.setDescription(description);
        }

        if (rating != null) {
            builder.setRating(rating);
        }

        return builder.build();
    }

    private Color matchColorToValue(String color) {
        if (color.matches("^(Белое|Голубое)")) {
            return Color.WHITE;
        } else if (color.startsWith("Розовое")) {
            return Color.ROSE;
        } else if (color.startsWith("Оранжевое")) {
            return Color.ORANGE;
        } else if (color.startsWith("Красное")) {
            return Color.RED;
        } else {
            return Color.UNRECOGNIZED;
        }
    }

    private Sugar matchSugarToValue(String sugar) {
        if (sugar.matches("^(Сухое|Брют).*")) {
            return Sugar.DRY;
        } else if (sugar.startsWith("Полусухое")) {
            return Sugar.MEDIUM_DRY;
        } else if (sugar.startsWith("Полусладкое")) {
            return Sugar.MEDIUM;
        } else if (sugar.startsWith("Сладкое")) {
            return Sugar.SWEET;
        } else {
            return Sugar.UNRECOGNIZED;
        }
    }
}
