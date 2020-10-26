package com.wine.to.up.winestyle.parser.service.domain.entity;

import javax.persistence.*;

@Entity
public class Image {
    @Id
    private Long id;

    @MapsId
    @OneToOne
    private Alcohol alcohol;

    @Lob
    @Column(columnDefinition="BYTEA")
    private byte[] image;
}
