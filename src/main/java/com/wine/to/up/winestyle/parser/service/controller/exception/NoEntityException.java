package com.wine.to.up.winestyle.parser.service.controller.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Класс-ошибка, который кидается
 * когда в базе данных нет запрашиваемой сущности
 */
@AllArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found.")
public class NoEntityException extends Exception {

    private static final long serialVersionUID = -8526013357444883928L;
    private final String entity;
    private final long id;

    /**
     * Конструктор ошибки отсутсвия сущности
     * @param entity тип сущности
     * @param id уникальный номер сущности
     * @return ошибка по сущности и номеру
     */
    public static NoEntityException createWith(String entity, long id) {
        return new NoEntityException(entity, id);
    }

    
    public String getMessage() {
        return "The server reported: " + entity + " with ID=" + id + " was not found.";
    }
}