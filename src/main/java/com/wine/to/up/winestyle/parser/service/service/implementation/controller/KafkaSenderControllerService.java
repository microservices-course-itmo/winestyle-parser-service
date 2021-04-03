package com.wine.to.up.winestyle.parser.service.service.implementation.controller;

import com.wine.to.up.winestyle.parser.service.controller.exception.ServiceIsBusyException;
import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.StatusService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.ServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderControllerService {
    private final StatusService statusService;
    private final KafkaService kafkaService;

    public void startSendingAlcohol() throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.KAFKASENDER)) {
            new Thread(() -> {
                kafkaService.sendAllAlcohol();
                statusService.release(ServiceType.KAFKASENDER);
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("Sending process alcohol to kafka in progress already");
        }
    }

    public void startSendingAlcohol(AlcoholType alcoholType) throws ServiceIsBusyException {
        if (statusService.tryBusy(ServiceType.KAFKASENDER)) {
            new Thread(() -> {
                kafkaService.sendAllAlcohol(alcoholType);
                statusService.release(ServiceType.KAFKASENDER);
            }).start();
        } else {
            throw ServiceIsBusyException.createWith("Sending process alcohol to kafka in progress already");
        }
    }

}
