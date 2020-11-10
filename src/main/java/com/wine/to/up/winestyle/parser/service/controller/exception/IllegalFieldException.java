package com.wine.to.up.winestyle.parser.service.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Класс-ошибка, который кидается
 * при запросе несуществуюющего поля сущности
 */
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Entity has no required field.")
public class IllegalFieldException extends Exception {

    private static final long serialVersionUID = -6933406005453479808L;
    private final String entityName;
    private final String field;

    /**
     * Конструктор ошибки несуществующего поля
     *
     * @param entityName имя сущности
     * @param field      несуществующее поле
     * @return сообщение ошибки
     */
    public static IllegalFieldException createWith(String entityName, String field) {
        return new IllegalFieldException(entityName, field);
    }

    public String getMessage() {
        return "The server reported: " + entityName + " entity has no field called \"" + field + "\".";
    }
}
