package com.wine.to.up.winestyle.parser.service.service.implementation.kafka;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderService implements KafkaService {
    private final RepositoryService repositoryService;
    private final KafkaSender kafkaSender;

    private Integer totalSended = 0;

    public void sendAllAlcohol() {
        List<Alcohol> alcohol = repositoryService.getAll();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all alcohol to Kafka at {};", startSendingProcess);

        sendAlcohol(alcohol);
        logKafkaSended("alcohol", totalSended, startSendingProcess);
        totalSended = 0;
    }

    public void sendAllWines() {
        List<Alcohol> wines = repositoryService.getAllWines();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all wines to Kafka at {};", startSendingProcess);
        sendAlcohol(wines);
        logKafkaSended(AlcoholType.WINE.toString(), totalSended, startSendingProcess);
        totalSended = 0;
    }

    public void sendAllSparkling() {
        List<Alcohol> sparkling = repositoryService.getAllSparkling();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all sparkling to Kafka at {};", startSendingProcess);

        sendAlcohol(sparkling);
        logKafkaSended(AlcoholType.SPARKLING.toString(), totalSended, startSendingProcess);
        totalSended = 0;
    }

    private void sendAlcohol(List<Alcohol> alcoholList) {
        alcoholList.forEach(alcohol -> totalSended += kafkaSender.sendAlcoholToKafka(alcohol));
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
}
