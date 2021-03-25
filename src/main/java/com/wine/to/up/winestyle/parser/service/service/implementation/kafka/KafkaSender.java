package com.wine.to.up.winestyle.parser.service.service.implementation.kafka;

import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaSender {
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    private final Director parserDirector;

    private ParserApi.WineParsedEvent.Builder kafkaMessageBuilder = ParserApi.WineParsedEvent.newBuilder();
    private Integer sended = 0;

    public Integer sendAlcoholToKafka(Alcohol alcohol) {
        try {
            kafkaMessageSender.sendMessage(kafkaMessageBuilder
                    .addWines(parserDirector
                            .fillKafkaMessageBuilder(alcohol, AlcoholType.valueOf(alcohol.getType())))
                    .build());
            WinestyleParserServiceMetricsCollector.incPublished();
            sended++;
        } catch (Exception ex) {
            log.error("Cannot send dataset to Kafka: id:{} {}", alcohol.getId(), alcohol.getType());
        }
        return sended;
    }
}