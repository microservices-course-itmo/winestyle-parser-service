package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.implementation.document.ScrapingService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainControllerService {
    private final StatusService statusService;
    private final ScrapingService scrapingService;

    public void startProxyInitJob(int maxTimeout) throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.PROXY)) {
            new Thread(() -> {
                try {
                    scrapingService.initProxy(maxTimeout);
                } finally {
                    statusService.release(ServiceType.PROXY);
                }
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("proxy initialization job is already running");
        }
    }
}
