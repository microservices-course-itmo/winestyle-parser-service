package com.wine.to.up.winestyle.parser.service.service.implementation.kafka;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.KafkaService;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.helpers.enums.AlcoholType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис, отвечающий за процесс отправки в кафку
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSenderService implements KafkaService {
    private final RepositoryService repositoryService;
    private final KafkaSender kafkaSender;

    /**
     * отправить алкоголь конкретного типа
     * @param alcoholType - тип алкоголя
     */
    public void sendAllAlcohol(AlcoholType alcoholType) {
        if (AlcoholType.WINE == alcoholType) {
            sendAllWines();
        } else if (AlcoholType.SPARKLING == alcoholType) {
            sendAllSparkling();
        }
    }

    /**
     * отправить все позиции алкоголя
     */
    public void sendAllAlcohol() {
        List<Alcohol> alcohol = repositoryService.getAll();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all alcohol to Kafka at {};", startSendingProcess);

        int totalSended = sendAlcohol(alcohol);
        logKafkaSended("alcohol", totalSended, startSendingProcess);
    }

    private void sendAllWines() {
        List<Alcohol> wines = repositoryService.getAllWines();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all wines to Kafka at {};", startSendingProcess);
        int totalSended = sendAlcohol(wines);
        logKafkaSended(AlcoholType.WINE.toString(), totalSended, startSendingProcess);
    }

    private void sendAllSparkling() {
        List<Alcohol> sparkling = repositoryService.getAllSparkling();

        LocalDateTime startSendingProcess = LocalDateTime.now();
        log.info("Start sending data of all sparkling to Kafka at {};", startSendingProcess);

        int totalSended = sendAlcohol(sparkling);
        logKafkaSended(AlcoholType.SPARKLING.toString(), totalSended, startSendingProcess);
    }

    private int sendAlcohol(List<Alcohol> alcoholList) {
        return alcoholList.stream().mapToInt(kafkaSender::sendAlcoholToKafka).sum();
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
