package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс, отвечающий за захват определенного сервиса как ресурса
 */
@Component
public class StatusService {
    private final AtomicBoolean isParserBusy = new AtomicBoolean(false);
    private final AtomicBoolean isProxyInitBusy = new AtomicBoolean(false);

    public void release(ServiceType serviceType) {
        switch (serviceType) {
            case PARSER:
                isParserBusy.set(false);
                break;
            case PROXY:
                isProxyInitBusy.set(false);
                break;
            default:
                throw new IllegalArgumentException("Passed service type is unsupported");
        }
    }

    /**
     * Пытается завладеть сервисом как ресурсом
     *
     * @return true в случае, если сервис был свободен и удалось занять ресурс; false в обратном случае
     */
    public boolean tryBusy(ServiceType serviceType) {
        switch (serviceType) {
            case PARSER:
                return isParserBusy.compareAndSet(false, true);
            case PROXY:
                return isProxyInitBusy.compareAndSet(false, true);
            default:
                throw new IllegalArgumentException("Passed service type is unsupported");
        }
    }
}
