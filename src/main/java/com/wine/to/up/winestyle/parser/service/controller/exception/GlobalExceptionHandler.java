package com.wine.to.up.winestyle.parser.service.controller.exception;

import com.wine.to.up.winestyle.parser.service.domain.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Предоставляет обработку исключений контроллеров всему сервису
     *
     * @param ex      Целевое исключение
     * @param request Текущий запрос
     */
    @ExceptionHandler({ServiceIsBusyException.class, NoEntityException.class, UnsupportedAlcoholTypeException.class,
            IllegalFieldException.class})
    public final ResponseEntity<ApiError> handleException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        if (ex instanceof ServiceIsBusyException) {
            HttpStatus status = HttpStatus.CONFLICT;
            ServiceIsBusyException serviceIsBusyException = (ServiceIsBusyException) ex;

            return handleServiceIsBusyException(serviceIsBusyException, headers, status, request);
        }

        if (ex instanceof NoEntityException) {
            HttpStatus status = HttpStatus.NOT_FOUND;
            NoEntityException noEntityException = (NoEntityException) ex;

            return handleNoEntityException(noEntityException, headers, status, request);
        }

        if (ex instanceof UnsupportedAlcoholTypeException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            UnsupportedAlcoholTypeException unsupportedAlcoholTypeException = (UnsupportedAlcoholTypeException) ex;

            return handleUnsupportedAlcoholTypeException(unsupportedAlcoholTypeException, headers, status, request);
        }

        if (ex instanceof IllegalFieldException) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            IllegalFieldException illegalFieldException = (IllegalFieldException) ex;

            return handleIllegalFieldException(illegalFieldException, headers, status, request);
        }

        return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * Обработчик исключения запуска сервера
     *
     * @param ex      Исключение занятости сервера
     * @param headers HTTP заголовки
     * @param status  HTTP статус
     * @param request запрос
     * @return
     */
    protected ResponseEntity<ApiError> handleServiceIsBusyException(ServiceIsBusyException ex,
                                                                    HttpHeaders headers, HttpStatus status,
                                                                    WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }

    /**
     * Обработчик исключения отутствия сущности
     *
     * @param ex      Ислючение отсутствия сущности
     * @param headers HTTP заголовки
     * @param status  HTTP статус
     * @param request запрос
     * @return
     */
    protected ResponseEntity<ApiError> handleNoEntityException(NoEntityException ex,
                                                               HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }

    /**
     * Обработчик исключения о неподдерживаемом типе алкоголя
     *
     * @param ex      Исключение о неподдерживаемом типе алкоголя
     * @param headers HTTP заголовки
     * @param status  HTTP статус
     * @param request Запрос
     * @return
     */
    protected ResponseEntity<ApiError> handleUnsupportedAlcoholTypeException(UnsupportedAlcoholTypeException ex,
                                                                             HttpHeaders headers, HttpStatus status,
                                                                             WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }

    /**
     * Обработчик исключения о несуществующих полях сущности
     *
     * @param ex      Исключения о несуществующих полях сущности
     * @param headers HTTP заголовки
     * @param status  HTTP статус
     * @param request Запрос
     * @return
     */
    protected ResponseEntity<ApiError> handleIllegalFieldException(IllegalFieldException ex,
                                                                   HttpHeaders headers, HttpStatus status,
                                                                   WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        return handleExceptionInternal(ex, new ApiError(errors), headers, status, request);
    }

    /**
     * Обработчик исключений по-умолчанию
     *
     * @param ex      Общее исключение
     * @param body    Список ошибок
     * @param headers HTTP заголовки
     * @param status  HTTP статус
     * @param request Запрос
     * @return
     */
    protected ResponseEntity<ApiError> handleExceptionInternal(Exception ex, @Nullable ApiError body,
                                                               HttpHeaders headers, HttpStatus status,
                                                               WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, SCOPE_REQUEST);
        }

        return new ResponseEntity<>(body, headers, status);
    }
}