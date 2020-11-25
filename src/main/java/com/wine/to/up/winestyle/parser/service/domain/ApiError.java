package com.wine.to.up.winestyle.parser.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Класс, содержащий в себе список сообщений об ошибках.
 */
@Setter
@Getter
@AllArgsConstructor
public class ApiError {
    private List<String> errors;
}
