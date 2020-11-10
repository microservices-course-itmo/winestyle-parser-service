package com.wine.to.up.winestyle.parser.service.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Класс, содержащий в себе список сообщений об ошибках.
 */
@Setter
@Getter
@RequiredArgsConstructor
public class ApiError {
    private List<String> errors;
}
