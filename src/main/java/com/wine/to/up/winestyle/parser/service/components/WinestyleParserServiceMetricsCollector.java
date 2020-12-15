package com.wine.to.up.winestyle.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */
@ConditionalOnProperty(value="kafka.enabled", havingValue="true", matchIfMissing=true)
@Component
public class WinestyleParserServiceMetricsCollector extends CommonMetricsCollector {
    private static final String SERVICE_NAME = "winestyle_parser_service";

    public WinestyleParserServiceMetricsCollector() {
        this(SERVICE_NAME);
    }

    private WinestyleParserServiceMetricsCollector(String serviceName) {
        super(serviceName);
    }
}
