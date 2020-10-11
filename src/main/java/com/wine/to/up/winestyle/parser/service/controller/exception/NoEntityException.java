package com.wine.to.up.winestyle.parser.service.controller.exception;

import io.swagger.annotations.ResponseHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Класс  ошибка, который кидается
 * когда в базе данных нет запрашиваемой сущности
 */
@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Entity not found.")
public class NoEntityException extends Exception {
    private final String cause;

    private NoEntityException(String cause) {
        this.cause = cause;
    }

    public static NoEntityException createWith(String cause) {
        return new NoEntityException(cause);
    }

    public String getMessage() {
        return "The server reported: " + cause;
    }
}