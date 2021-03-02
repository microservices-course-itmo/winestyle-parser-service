package com.wine.to.up.winestyle.parser.service.service.implementation.kafka;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderService implements KafkaService {
    private final Director parserDirector;
    private final RepositoryService repositoryService;
    private final KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;

//    @Value("${spring.task.execution.pool.size}")
//    private int maxThreadCount;

    private ParserApi.Wine.Builder kafkaMessageBuilder;
//
//    private ExecutorService kafkaSendAllThreadPool;
//    private final ThreadFactory kafkaSendAllThreadFactory = new ThreadFactoryBuilder()
//            .setNameFormat("Kafka-Sender_All-Alcohols-%d")
//            .build();
//
//    private ExecutorService initPool(int maxThreadCount, ThreadFactory threadFactory) {
//        return Executors.newFixedThreadPool(maxThreadCount, threadFactory);
//    }
//
//    private void renewPools() {
//        if (kafkaSendAllThreadPool.isShutdown()) {
//            kafkaSendAllThreadPool = initPool(maxThreadCount, kafkaSendAllThreadFactory);
//        }
//    }
//
//    @PostConstruct
//    private void initPools() {
//        kafkaSendAllThreadPool = initPool(maxThreadCount, kafkaSendAllThreadFactory);
//    }

    public void sendAllAlcohols() {
//        renewPools();
        List<Alcohol> alcohols = repositoryService.getAll();
        LocalDateTime startSendingProcess = LocalDateTime.now();

        log.info("Start sending info with all alcohols to Kafka at {};", startSendingProcess);

//        List<Future<Integer>> sendingFutures = new ArrayList<>();
//        sendingFutures.add(kafkaSendAllThreadPool.submit(new KafkaSender()));

        alcohols.parallelStream().forEach(alcohol -> {
            kafkaMessageBuilder = parserDirector.fillKafkaMessageBuilder(alcohol, AlcoholType.valueOf(alcohol.getType()));
            kafkaMessageSender.sendMessage(kafkaMessageBuilder);
        });

        LocalDateTime endSendingProcess = LocalDateTime.now();
        log.info("End sending info with all alcohols to Kafka at {}; Total time of sending data {}h:{}m:{}s;",
                endSendingProcess,
                endSendingProcess.getHour() - startSendingProcess.getHour(),
                endSendingProcess.getMinute() - startSendingProcess.getHour(),
                endSendingProcess.getSecond() - startSendingProcess.getSecond());
    }


    public void sendAllWines() {
        List<Alcohol> wines = repositoryService.getAllWines();
        LocalDateTime startSendingProcess = LocalDateTime.now();

        log.info("Start sending info to Kafka at {}, ", startSendingProcess);

        wines.parallelStream().forEach(wine -> {
            kafkaMessageBuilder = parserDirector.fillKafkaMessageBuilder(wine, AlcoholType.WINE);
            kafkaMessageSender.sendMessage(kafkaMessageBuilder);
        });

        LocalDateTime endSendingProcess = LocalDateTime.now();
        log.info("End sending info with all wines to Kafka at {}; Total time of sending data {}h:{}m:{}s;",
                endSendingProcess,
                endSendingProcess.getHour() - startSendingProcess.getHour(),
                endSendingProcess.getMinute() - startSendingProcess.getHour(),
                endSendingProcess.getSecond() - startSendingProcess.getSecond());
    }

    public void sendAllSparkling() {
        List<Alcohol> sparklings = repositoryService.getAllSparkling();
        LocalDateTime startSendingProcess = LocalDateTime.now();

        log.info("Start sending info to Kafka at {}, ", startSendingProcess);

        sparklings.parallelStream().forEach(sparkling -> {
            kafkaMessageBuilder = parserDirector.fillKafkaMessageBuilder(sparkling, AlcoholType.WINE);
            kafkaMessageSender.sendMessage(kafkaMessageBuilder);
        });

        LocalDateTime endSendingProcess = LocalDateTime.now();
        log.info("End sending info with all sparkling to Kafka at {}; Total time of sending data {}h:{}m:{}s;",
                endSendingProcess,
                endSendingProcess.getHour() - startSendingProcess.getHour(),
                endSendingProcess.getMinute() - startSendingProcess.getHour(),
                endSendingProcess.getSecond() - startSendingProcess.getSecond());
    }

//    @RequiredArgsConstructor
//    private class KafkaSender implements Callable<Integer> {
//
//        @Override
//        public Integer call() throws Exception {
//            return null;
//        }
//    }
}
