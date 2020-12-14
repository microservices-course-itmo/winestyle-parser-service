package com.wine.to.up.winestyle.parser.service.components;

import com.google.common.util.concurrent.AtomicDouble;
import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

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

    private static final String DETAILS_FETCHING_DURATION_SUMMARY = "wine_details_fetching_duration";
    private static final String PAGE_FETCHING_DURATION_SUMMARY = "wine_page_fetching_duration";
    private static final String PAGE_PARSING_DURATION_SUMMARY = "wine_page_parsing_duration";
    private static final String DETAILS_PARSING_DURATION_SUMMARY = "wine_details_parsing_duration";

    private static final String PARSER_NAME = "winestyle_parser_service";
    public static final String PARSER_NAME_TAG = "winestyle";

    private static final Counter parsingStartedCounter = Metrics.counter(PARSING_STARTED_COUNTER, PARSER_NAME_TAG, PARSER_NAME);
    private static final Counter parsingCompleteCounter = Metrics.counter(PARSING_COMPLETE_COUNTER, PARSER_NAME_TAG, PARSER_NAME);
    private static final Counter publishedCounter = Metrics.counter(PUBLISHED_COUNTER, PARSER_NAME_TAG, PARSER_NAME);

    private static final AtomicInteger parsingInProgress = Metrics.gauge(
            IN_PROGRESS_GAUGE,
            List.of(Tag.of(PARSER_NAME_TAG, PARSER_NAME)),
            new AtomicInteger(0)
    );
    private static final AtomicDouble sinceLastSucceed = Metrics.gauge(
            TIME_SINCE_LAST_SUCCEED_GAUGE,
            List.of(Tag.of(PARSER_NAME_TAG, PARSER_NAME)),
            new AtomicDouble(0)
    );

    private static final DistributionSummary pageFetchingDurationSummary = Metrics.summary(
            PAGE_FETCHING_DURATION_SUMMARY,
            List.of(Tag.of(PARSER_NAME_TAG, PARSER_NAME))
    );
    private static final DistributionSummary detailsFetchingDurationSummary = Metrics.summary(
            DETAILS_FETCHING_DURATION_SUMMARY,
            List.of(Tag.of(PARSER_NAME_TAG, PARSER_NAME))
    );
    private static final DistributionSummary pageParsingDurationSummary = Metrics.summary(
            PAGE_PARSING_DURATION_SUMMARY,
            List.of(Tag.of(PARSER_NAME_TAG, PARSER_NAME))
    );
    private static final DistributionSummary detailsParsingDurationSummary = Metrics.summary(
            DETAILS_PARSING_DURATION_SUMMARY,
            List.of(Tag.of(PARSER_NAME_TAG, PARSER_NAME))
    );

    public WinestyleParserServiceMetricsCollector(@Value("${spring.kafka.metrics.service-name}") String serviceName) {
        super(serviceName);
    }

    public static void incParsingStarted() {
        parsingStartedCounter.increment();
    }

    public static void incParsingComplete() {
        parsingCompleteCounter.increment();
    }

    public static void incPublished() {
        publishedCounter.increment();
    }

    public static void updateInProgress(int currentValue) {
        Objects.requireNonNull(parsingInProgress).set(currentValue);
    }

    public static void updateSinceLastSucceed(double currentValue) {
        Objects.requireNonNull(sinceLastSucceed).set(currentValue);
    }

    public static void sumPageFetchingDuration(LocalDateTime timeBeforeFetching, LocalDateTime timeAfterFetching) {
        pageFetchingDurationSummary.record(Duration.between(timeBeforeFetching, timeAfterFetching).toNanos() / 1e9d);
    }

    public static void sumDetailsFetchingDuration(LocalDateTime timeBeforeFetching, LocalDateTime timeAfterFetching) {
        detailsFetchingDurationSummary.record(Duration.between(timeBeforeFetching, timeAfterFetching).toNanos() / 1e9d);
    }

    public static void sumPageParsingDuration(LocalDateTime timeBeforeParsing, LocalDateTime timeAfterParsing) {
        pageParsingDurationSummary.record(Duration.between(timeBeforeParsing, timeAfterParsing).toNanos() / 1e9d);
    }

    public static void sumDetailsParsingDuration(LocalDateTime timeBeforeParsing, LocalDateTime timeAfterParsing) {
        detailsParsingDurationSummary.record(Duration.between(timeBeforeParsing, timeAfterParsing).toNanos() / 1e9d);
    }
}
