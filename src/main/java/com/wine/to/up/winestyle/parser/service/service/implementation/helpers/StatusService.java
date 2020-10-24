package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс, отвечающий за проверку наличия парсинга
 */
@Component
public class StatusService {
    private final HashMap<String, Boolean> SERVICE_BUSY_STATUS = new HashMap<>(
            Map.of("wine", false, "sparkling", false));


    /**
     * Проверка парсинга
     * @param alcoholType Тип напитка
     * @return Идет ли парсинг заданной категории
     */
    public boolean isBusy(String alcoholType) {
        return !SERVICE_BUSY_STATUS.get(alcoholType);
    }

    public void busy(String alcoholType) {
        SERVICE_BUSY_STATUS.replace(alcoholType, !SERVICE_BUSY_STATUS.get(alcoholType));
    }
}
