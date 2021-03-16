package com.wine.to.up.winestyle.parser.service.service.implementation.kafka;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.components.WinestyleParserServiceMetricsCollector;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderService implements KafkaService {
    private final Director parserDirector;
    private final RepositoryService repositoryService;
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;

    @Value("${spring.task.execution.pool.size}")
    private int maxThreadCount;
    @Value("${spring.jsoup.scraping.proxy-timeout.millis}")
    private int timeout;

    private ExecutorService kafkaSendAllThreadPool;
    private final ThreadFactory kafkaSendAllThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Kafka-Sender-%d")
            .build();

    private ExecutorService initPool(int maxThreadCount, ThreadFactory threadFactory) {
        return Executors.newFixedThreadPool(maxThreadCount, threadFactory);
    }

    private void renewPools() {
        if (kafkaSendAllThreadPool.isShutdown()) {
            kafkaSendAllThreadPool = initPool(maxThreadCount, kafkaSendAllThreadFactory);
        }
    }

    @PostConstruct
    private void initPools() {
        kafkaSendAllThreadPool = initPool(maxThreadCount, kafkaSendAllThreadFactory);
    }

    public void sendAllalcohol() {
        renewPools();
        List<Alcohol> alcohol = repositoryService.getAll();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all alcohol to Kafka at {};", startSendingProcess);

        int totalSended = 0;
        try {
            totalSended = sendalcohol(alcohol);
        } catch (InterruptedException e) {
            log.warn("The process of sending alcohol to kafka was interrupted!");
        }
        logKafkaSended("alcohol", totalSended, startSendingProcess);
    }

    public void sendAllWines() {
        renewPools();
        List<Alcohol> wines = repositoryService.getAllWines();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all wines to Kafka at {};", startSendingProcess);

        int totalSended = 0;
        try {
            totalSended = sendalcohol(wines);
        } catch (InterruptedException e) {
            log.warn("The process of sending wines to kafka was interrupted!");
        }
        logKafkaSended(AlcoholType.WINE.toString(), totalSended, startSendingProcess);
    }

    public void sendAllSparkling() {
        renewPools();
        List<Alcohol> sparkling = repositoryService.getAllSparkling();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all sparkling to Kafka at {};", startSendingProcess);

        int totalSended = 0;
        try {
            totalSended = sendalcohol(sparkling);
        } catch (InterruptedException e) {
            log.warn("The process of sending sparkling to kafka was interrupted!");
        }
        logKafkaSended(AlcoholType.SPARKLING.toString(), totalSended, startSendingProcess);
    }

    private Integer sendalcohol(List<Alcohol> alcoholList) throws InterruptedException {
        List<Future<Integer>> sendingFutures = new ArrayList<>();
        Integer totalSended = 0;

        try {
            alcoholList.parallelStream().forEach(alcohol -> {
                sendingFutures.add(kafkaSendAllThreadPool.submit(new KafkaSender(parserDirector, alcohol)));
            });
        } finally {
            kafkaSendAllThreadPool.shutdown();
            kafkaSendAllThreadPool.awaitTermination(timeout, TimeUnit.MILLISECONDS);

            for (Future<Integer> future : sendingFutures) {
                try {
                    totalSended += future.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    log.error("Cannot execute sending alcohol to kafka");
                }
            }
        }
        return totalSended;
    }

    private void logKafkaSended(String alcoholType, Integer total, LocalDateTime startTime) {
        long hoursPassed;
        long minutesPart;
        long secondsPart;
        Duration timePassed = java.time.Duration.between((startTime), LocalDateTime.now());

        hoursPassed = timePassed.toHours();
        minutesPart = (timePassed.toMinutes() - hoursPassed * 60);
        secondsPart = (timePassed.toSeconds() - hoursPassed * 3600 - minutesPart * 60);

        log.info("Sending {} to kafka: in {} hours {} minutes {} seconds. Total sended {} alcohol",
                alcoholType, hoursPassed, minutesPart, secondsPart, total);
    }

    @RequiredArgsConstructor
    private class KafkaSender implements Callable<Integer> {
        private final Director parserDirector;
        private final Alcohol alcohol;

        private ParserApi.WineParsedEvent.Builder kafkaMessageBuilder = ParserApi.WineParsedEvent.newBuilder();
        private Integer sended = 0;

        private Integer sendAlcoholToKafka() {
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

        @Override
        public Integer call() throws Exception {
            return sendAlcoholToKafka();
        }
    }
}
