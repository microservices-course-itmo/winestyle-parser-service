package com.wine.to.up.winestyle.parser.service.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ApiError {
    private List<String> errors;

    public ApiError(List<String> errors) {
        this.errors = errors;
    }
}
