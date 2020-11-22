package com.wine.to.up.winestyle.parser.service.controller.exception;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NoEntityExceptionTest {

    @Test
    void getMessage() {
        Long actualId = 10000000L;
        String actualClassName = Alcohol.class.getSimpleName().toLowerCase();

        Throwable exception = assertThrows(NoEntityException.class,
                ()->{
                    throw NoEntityException.createWith(actualClassName, actualId, null);
                });
        assertEquals("The server reported: " + actualClassName + " with ID=" + actualId + " was not found.",
                exception.getMessage());
    }
}