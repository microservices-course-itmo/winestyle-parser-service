package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;

public interface KafkaService {
    void sendAllAlcohol();
    void sendAllAlcohol(AlcoholType alcoholType);
}
