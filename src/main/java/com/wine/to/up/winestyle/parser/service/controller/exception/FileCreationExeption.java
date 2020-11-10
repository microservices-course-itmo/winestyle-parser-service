package com.wine.to.up.winestyle.parser.service.controller.exception;

public class FileCreationExeption extends RuntimeException {

    private static final long serialVersionUID = -4405828993451989530L;

    /**
     * Ошибка невозможности создания файла
     */

    @Override
    public String getMessage() {
        return "Cannot write database to file!";
    }

}
