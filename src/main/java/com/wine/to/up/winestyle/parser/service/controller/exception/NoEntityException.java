package com.wine.to.up.winestyle.parser.service.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Класс-ошибка, который кидается
 * когда в базе данных нет запрашиваемой сущности
 */
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found.")
public class NoEntityException extends Exception {

    private static final long serialVersionUID = -8526013357444883928L;
    private final String entity;
    private final Long id;
    private final String url;

    /**
     * Конструктор ошибки отсутсвия сущности
     *
     * @param entity тип сущности
     * @param id     уникальный номер сущности
     * @return ошибка по сущности и номеру
     */
    public static NoEntityException createWith(String entity, Long id, String url) {
        return new NoEntityException(entity, id, url);
    }

    public String getMessage() {
        if (url == null) {
            return "The server reported: " + entity + " with ID=" + id + " was not found.";
        } else {
            return "The server reported: " + entity + " with URL=" + url + " was not found.";
        }
    }
}