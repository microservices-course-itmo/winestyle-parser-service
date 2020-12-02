package com.wine.to.up.winestyle.parser.service.domain.entity;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class Timing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastSucceedDate;
}
