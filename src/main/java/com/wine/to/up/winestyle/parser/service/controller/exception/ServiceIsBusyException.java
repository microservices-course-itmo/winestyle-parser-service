package com.wine.to.up.winestyle.parser.service.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Класс-ошибка, который кидается при попытке запустить парсинг при уже запущеном парсинге.
 */
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Service is busy.")
public class ServiceIsBusyException extends Exception {

    private static final long serialVersionUID = -1034054051600551718L;
    private final String cause;
    private final String alcoholType;

    /**
     * Конструктор ошибки сервера
     * @param cause причина
     * @return ошибку сервера
     */
    public static ServiceIsBusyException createWith(String cause, String alcoholType) {
        return new ServiceIsBusyException(cause, alcoholType);
    }

    public String getMessage() {
        return "The server reported: " + alcoholType + cause;
    }
}