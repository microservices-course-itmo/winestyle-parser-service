package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
public class Image {
    @Id
    private Long id;

    @MapsId
    @OneToOne
    private Alcohol alcohol;

    @Getter
    @Lob
    @Column(columnDefinition="BYTEA")
    @Getter
    private byte[] data;
}
