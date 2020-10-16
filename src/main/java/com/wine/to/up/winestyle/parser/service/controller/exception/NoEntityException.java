package com.wine.to.up.winestyle.parser.service.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Класс  ошибка, который кидается
 * когда в базе данных нет запрашиваемой сущности
 */
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Entity not found.")
public class NoEntityException extends Exception {
    private final String entity;
    private final long id;

    public static NoEntityException createWith(String entity, long id) {
        return new NoEntityException(entity, id);
    }

    public String getMessage() {
        return "The server reported: " + entity + " with ID=" + id + " was not found.";
    }
}