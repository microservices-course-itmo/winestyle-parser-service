package com.wine.to.up.winestyle.parser.service.service.implementation.helpers;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StatusService {
    private final HashMap<String, Boolean> SERVICE_BUSY_STATUS =
            new HashMap<>(Map.of("wine", false, "sparkling", false));

    public boolean isBusy(String alcoholType) {
        return !SERVICE_BUSY_STATUS.get(alcoholType);
    }

    public void busy(String alcoholType) {
        SERVICE_BUSY_STATUS.replace(alcoholType, !SERVICE_BUSY_STATUS.get(alcoholType));
    }
}
