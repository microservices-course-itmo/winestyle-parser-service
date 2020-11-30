package com.wine.to.up.winestyle.parser.service.controller.exception;

import com.wine.to.up.winestyle.parser.service.domain.ApiError;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Предоставляет обработку исключений контроллеров всему сервису
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Обработчик исключения запуска сервера
     *
     * @param ex      Исключение занятости сервера
     * @param request Запрос
     * @return возвращаемая клиенту ResponseEntity
     */
    @ExceptionHandler(ServiceIsBusyException.class)
    protected ResponseEntity<ApiError> handleServiceIsBusyException(ServiceIsBusyException ex, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    /**
     * Обработчик исключения отутствия сущности
     *
     * @param ex      Ислючение отсутствия сущности
     * @param request Запрос
     * @return возвращаемая клиенту ResponseEntity
     */
    @ExceptionHandler(NoEntityException.class)
    protected ResponseEntity<ApiError> handleNoEntityException(NoEntityException ex, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    /**
     * Обработчик исключения о неподдерживаемом типе алкоголя
     *
     * @param ex      Исключение о неподдерживаемом типе алкоголя
     * @param request Запрос
     * @return возвращаемая клиенту ResponseEntity
     */
    @ExceptionHandler(UnsupportedAlcoholTypeException.class)
    protected ResponseEntity<ApiError> handleUnsupportedAlcoholTypeException(UnsupportedAlcoholTypeException ex,
                                                                             WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Обработчик исключения о неподдерживаемом типе алкоголя
     *
     * @param ex      Исключение о неподдерживаемом типе алкоголя
     * @param request Запрос
     * @return возвращаемая клиенту ResponseEntity
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex,
                                                                              WebRequest request) {
        List<String> errors;
        if(Objects.requireNonNull(ex.getRequiredType()).equals(AlcoholType.class)) {
            errors = Collections.singletonList("The server reported: " + ex.getValue() + " alcohol type is not supported");
        } else {
            errors = Collections.singletonList("The server reported: " + ex.getValue() + " city is not supported");
        }
        return handleExceptionInternal(ex, new ApiError(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Обработчик исключения о несуществующих полях сущности
     *
     * @param ex      Исключения о несуществующих полях сущности
     * @param request Запрос
     * @return возвращаемая клиенту ResponseEntity
     */
    @ExceptionHandler(IllegalFieldException.class)
    protected ResponseEntity<ApiError> handleIllegalFieldException(IllegalFieldException ex, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Обработчик исключений по-умолчанию
     *
     * @param ex      Общее исключение
     * @param body    Список ошибок
     * @param headers HTTP заголовки
     * @param status  HTTP статус
     * @param request Запрос
     * @return возвращаемая клиенту ResponseEntity
     */
    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, @Nullable ApiError body,
                                                               HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}