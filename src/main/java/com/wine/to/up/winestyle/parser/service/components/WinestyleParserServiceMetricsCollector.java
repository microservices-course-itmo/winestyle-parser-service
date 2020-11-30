package com.wine.to.up.winestyle.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */
@Component
public class WinestyleParserServiceMetricsCollector extends CommonMetricsCollector {
    private static final String PARSING_STARTED_COUNTER = "parsing_started_total";
    private static final String PARSING_COMPLETE_COUNTER = "parsing_complete_total";
    private static final String PUBLISHED_COUNTER = "wines_published_to_kafka_count";

    public  static final String PARSER_NAME_TAG = "winestyle";

    public WinestyleParserServiceMetricsCollector(@Value("${spring.kafka.metrics.service-name}") String serviceName) {
        super(serviceName);
    }

    public void incParsingStarted(String parserName) {
        Metrics.counter(PARSING_STARTED_COUNTER, PARSER_NAME_TAG, parserName).increment();
    }

    public void incParsingComplete(String parserName) {
        Metrics.counter(PARSING_COMPLETE_COUNTER, PARSER_NAME_TAG, parserName).increment();
    }

    public void incPublished(String parserName) {
        Metrics.counter(PUBLISHED_COUNTER, PARSER_NAME_TAG, parserName).increment();
    }
}
