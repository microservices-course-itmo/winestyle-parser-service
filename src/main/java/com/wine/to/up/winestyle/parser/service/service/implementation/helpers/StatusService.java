package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс, отвечающий за проверку наличия парсинга
 */
@Component
public class StatusService {
    private final AtomicBoolean isBusy = new AtomicBoolean(false);

    public void release() {
        isBusy.set(false);
    }

    /**
     * Пытается завладеть парсером как ресурсом
     * @return true в случае, если парсер был свободен и удалось занять ресурс; false в обратном случае
     */
    public boolean tryBusy() {
        return isBusy.compareAndSet(false, true);
    }
}
