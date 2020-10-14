package com.wine.to.up.winestyle.parser.service.messaging.serialization;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wine.to.up.parser.common.api.schema.UpdateProducts;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * Deserializer for {@link UpdateProducts.UpdateProductsMessage}
 */
@Slf4j
public class EventDeserializer implements Deserializer<UpdateProducts.UpdateProductsMessage> {
    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateProducts.UpdateProductsMessage deserialize(String topic, byte[] bytes) {
        try {
            return UpdateProducts.UpdateProductsMessage.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to deserialize message from topic: {}. {}", topic, e);
            return null;
        }
    }
}
