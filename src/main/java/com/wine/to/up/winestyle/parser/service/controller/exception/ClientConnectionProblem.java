package com.wine.to.up.winestyle.parser.service.controller.exception;

public class ClientConnectionProblem extends RuntimeException {
    
    private static final long serialVersionUID = 5071397068069570806L;

    /**
     * Ошибка невозможности доступа к клиенту. Например, когда клиент закрыл сессию.
     */

    @Override
    public String getMessage() {
        return "Error while feeding file to outputStream";
    }

}
