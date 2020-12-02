package com.wine.to.up.winestyle.parser.service.components;

import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This Class expose methods for recording specific metrics
 * It changes metrics of Micrometer and Prometheus simultaneously
 * Micrometer's metrics exposed at /actuator/prometheus
 * Prometheus' metrics exposed at /metrics-prometheus
 */
@Component
public final class WinestyleParserServiceMetricsCollector extends CommonMetricsCollector {
    private static final String PARSING_STARTED_COUNTER = "parsing_started_total";
    private static final String PARSING_COMPLETE_COUNTER = "parsing_complete_total";
    private static final String PUBLISHED_COUNTER = "wines_published_to_kafka_count";

    private static final String IN_PROGRESS_GAUGE = "parsing_in_progress";
    private static final String TIME_SINCE_LAST_SUCCEED_GAUGE = "time_since_last_succeeded_parsing";

    private static final String PARSING_PROCESS_DURATION_SUMMARY = "parsing_process_duration";
    private static final String DETAILS_FETCHING_DURATION_SUMMARY = "wine_details_fetching_duration";
    private static final String PAGE_FETCHING_DURATION_SUMMARY = "wine_page_fetching_duration";
    private static final String PAGE_PARSING_DURATION_SUMMARY = "wine_page_parsing_duration";
    private static final String DETAILS_PARSING_DURATION_SUMMARY = "wine_details_parsing_duration";

    public static final String PARSER_NAME_TAG = "winestyle";

    private WinestyleParserServiceMetricsCollector(@Value("${spring.kafka.metrics.service-name}") String serviceName) {
        super(serviceName);
    }

    public static void incParsingStarted(String parserName) {
        Metrics.counter(PARSING_STARTED_COUNTER, PARSER_NAME_TAG, parserName).increment();
    }

    public static void incParsingComplete(String parserName) {
        Metrics.counter(PARSING_COMPLETE_COUNTER, PARSER_NAME_TAG, parserName).increment();
    }

    public static void incPublished(String parserName) {
        Metrics.counter(PUBLISHED_COUNTER, PARSER_NAME_TAG, parserName).increment();
    }

    public static void gaugeInProgress(String parserName, Integer currentValue) {
        Metrics.gauge(IN_PROGRESS_GAUGE, List.of(Tag.of(PARSER_NAME_TAG, parserName)), currentValue);
    }

    public static void gaugeSinceLastSucceed(String parserName, Long currentValue) {
        Metrics.gauge(TIME_SINCE_LAST_SUCCEED_GAUGE, List.of(Tag.of(PARSER_NAME_TAG, parserName)), currentValue);
    }

    public static void sumProcessParsing(String parserName) {
        Metrics.summary(PARSING_PROCESS_DURATION_SUMMARY, List.of(Tag.of(PARSER_NAME_TAG, parserName)));
    }

    public static void sumDetailsFetching(String parserName) {
        Metrics.summary(DETAILS_FETCHING_DURATION_SUMMARY, List.of(Tag.of(PARSER_NAME_TAG, parserName)));
    }

    public static void sumPageFetching(String parserName) {
        Metrics.summary(PAGE_FETCHING_DURATION_SUMMARY, List.of(Tag.of(PARSER_NAME_TAG, parserName)));
    }

    public static void sumPageParsing(String parserName) {
        Metrics.summary(PAGE_PARSING_DURATION_SUMMARY, List.of(Tag.of(PARSER_NAME_TAG, parserName)));
    }

    public static void sumDetailsParsing(String parserName) {
        Metrics.summary(DETAILS_PARSING_DURATION_SUMMARY, List.of(Tag.of(PARSER_NAME_TAG, parserName)));
    }
}
