package com.wine.to.up.winestyle.parser.service.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс, содержащий в себе список сообщений об ошибках.
 */
@Setter
@Getter
public class ApiError {
    private List<String> errors;

    /**
     * Конструктор
     *
     * @param errors список с ошибками
     */
    public ApiError(List<String> errors) {
        this.errors = errors;
    }
}
