package com.wine.to.up.winestyle.parser.service.service;

import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;

public interface Director {
    Alcohol makeAlcohol(Parser parser, String mainPageUrl, String productUrl, AlcoholType alcoholType);

    ParserApi.Wine.Builder getKafkaMessageBuilder();

    ParserApi.Wine.Builder fillKafkaMessageBuilder(Alcohol source, AlcoholType type);
}
