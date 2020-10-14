package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.*;

import javax.persistence.*;

import com.wine.to.up.parser.common.api.schema.UpdateProducts;
import com.wine.to.up.parser.common.api.schema.UpdateProducts.Product.Color;
import com.wine.to.up.parser.common.api.schema.UpdateProducts.Product.Sugar;

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

    public UpdateProducts.Product asProduct() {
        UpdateProducts.Product.Builder builder = UpdateProducts.Product.newBuilder();
        if (name != null)
            builder.setName(name);
        if (url != null)
            builder.setLink(url);
        // TODO set image
        // if (imageUrl != null)
        //     builder.setImage(imageUrl);
        if (cropYear != null)
            builder.setYear(cropYear);
        if (manufacturer != null)
            builder.setManufacturer(manufacturer);
        if (brand != null)
            builder.setBrand(brand);
        if (color != null)
            builder.setColor(color.equals("Красное") ? Color.RED : 
                    color.equals("Белое") ? Color.WHITE :
                    color.equals("Розовое") ? Color.ROSE :
                    color.equals("Оранжевое") ? Color.ORANGE :
                    Color.UNRECOGNIZED);
        if (country != null)
            builder.setCountry(country);
        if (region != null)
            builder.addRegion(region); // FIXME addRegion ?
        if (volume != null)
            builder.setCapacity((float) (double) volume);
        if (strength != null)
            builder.setStrength(Float.parseFloat(strength.substring(0, strength.length() - 1)));
        if (sugar != null)
            builder.setSugar(sugar.equals("Сухое") ? Sugar.DRY :
                    sugar.equals("Полусухое") ? Sugar.MEDIUM_DRY :
                    sugar.equals("Полусладкое") ? Sugar.MEDIUM :
                    sugar.equals("Сладкое") ? Sugar.SWEET :
                    Sugar.UNRECOGNIZED);
        if (price != null)
            builder.setNewPrice((float) price.doubleValue());
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
            builder.setRating((float) (double) rating);
        return builder.build();
    }
}
