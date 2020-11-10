package com.wine.to.up.winestyle.parser.service.controller.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Класс-ошибка, который кидается при попытке запустить парсинг с неподдерживаемым/неверным типом алкоголя.
 */
@RequiredArgsConstructor
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Service is not supporting such alcohol type.")
public class UnsupportedAlcoholTypeException extends Exception {
    private static final long serialVersionUID = 210512838564413311L;

    private final String cause;
    private final String alcoholType;

    public static UnsupportedAlcoholTypeException createWith(String cause, String alcoholType) {
        return new UnsupportedAlcoholTypeException(cause, alcoholType);
    }

    @Override
    public String getMessage() {
        return "The server reported: " + alcoholType + " alcohol type " + cause;
    }
}
