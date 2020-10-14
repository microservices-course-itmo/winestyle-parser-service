package com.wine.to.up.winestyle.parser.service.messaging;

import com.wine.to.up.commonlib.messaging.KafkaMessageHandler;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WineStyleKafkaMessageHandler implements KafkaMessageHandler<UpdateProducts.UpdateProductsMessage> {

    @Override
    public void handle(UpdateProducts.UpdateProductsMessage message) {
        log.info("Message ({}) received: {}", message.getClass().getSimpleName(), message.getAllFields());
    }
}