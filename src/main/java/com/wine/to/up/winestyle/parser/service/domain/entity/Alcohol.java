package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;

import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import com.wine.to.up.parser.common.api.schema.UpdateProducts.Product.Color;
import com.wine.to.up.parser.common.api.schema.UpdateProducts.Product.Sugar;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
    private String strength;

    @Column(columnDefinition = "varchar(20)")
    private String sugar;

    @Setter
    @Column
    private Float price;

    @Column(columnDefinition = "varchar(125)")
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

    /**
     * Преобразование нашего класса Wine в общий для парсеров класс Product
     * @return Product
     */
    public UpdateProducts.Product asProduct() {
        UpdateProducts.Product.Builder builder = UpdateProducts.Product.newBuilder();
        if (name != null)
            builder.setName(name);
        if (url != null)
            builder.setLink(url);
        // TODO set image
        if (image != null)
             builder.setImage(String.valueOf(image));
        if (cropYear != null)
            builder.setYear(cropYear);
        if (manufacturer != null)
            builder.setManufacturer(manufacturer);
        if (brand != null)
            builder.setBrand(brand);
        if (color != null)
            builder.setColor(color.startsWith("Красное") ? Color.RED :
                    color.matches("^(Белое|Голубое)") ? Color.WHITE :
                    color.startsWith("Розовое") ? Color.ROSE :
                    color.startsWith("Оранжевое") ? Color.ORANGE :
                    Color.UNRECOGNIZED);
        if (country != null)
            builder.setCountry(country);
        if (region != null)
            builder.addRegion(region); // FIXME addRegion ?
        if (volume != null)
            builder.setCapacity(volume);
        if (strength != null)
            builder.setStrength(strength.matches("^Б.+") ? 0.f :
                    Float.parseFloat(strength.substring(0, strength.length() - 1)));
        if (sugar != null)
            builder.setSugar(sugar.matches("^(Сухое|Брют).*") ? Sugar.DRY :
                    sugar.startsWith("Полусухое") ? Sugar.MEDIUM_DRY :
                    sugar.startsWith("Полусладкое") ? Sugar.MEDIUM :
                    sugar.startsWith("Сладкое") ? Sugar.SWEET :
                    Sugar.UNRECOGNIZED);
        if (price != null)
            builder.setNewPrice(price);
        if (grape != null)
            builder.addGrapeSort(grape); // FIXME addGrapeSort ?
        if (taste != null)
            builder.setTaste(taste);
        if (aroma != null)
            builder.setFlavor(aroma);
        if (foodPairing != null)
            builder.setGastronomy(foodPairing);
        if (description != null)
            builder.setDescription(description);
        if (rating != null)
            builder.setRating(rating);
        return builder.build();
    }
}
