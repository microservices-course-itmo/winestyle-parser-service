package com.wine.to.up.winestyle.parser.service.service.implementation.kafka;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wine.to.up.commonlib.messaging.KafkaMessageSender;
import com.wine.to.up.parser.common.api.schema.ParserApi;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.Director;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class KafkaSenderServiceTest {
    @Mock
    Director parserDirector;
    @Mock
    RepositoryService repositoryService;
    @Mock
    KafkaMessageSender<ParserApi.WineParsedEvent> kafkaMessageSender;
    ThreadFactory kafkaSendAllThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Kafka-Sender-%d")
            .build();
    ExecutorService kafkaSendAllThreadPool;
    @InjectMocks
    KafkaSenderService kafkaSenderService;

    Alcohol testAlcohol =  Alcohol.builder()
            .id(null).name("test").type("test").url("test").imageUrl("test").cropYear(1990)
                .manufacturer("test").brand("test").color("Красное").country("test").region("test")
                .volume(1F).strength(1F).sugar("Сухое").price(1F)
                .grape("test").taste("test").aroma("test").foodPairing("test")
                .description("test").rating(1F)
                .build();
    List<Alcohol> testAlcoholList = List.of(testAlcohol, testAlcohol);

    @BeforeEach
    void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(kafkaSenderService, "parserDirector", parserDirector);
        ReflectionTestUtils.setField(kafkaSenderService, "repositoryService", repositoryService);
        ReflectionTestUtils.setField(kafkaSenderService, "kafkaMessageSender", kafkaMessageSender);
        ReflectionTestUtils.setField(kafkaSenderService, "maxThreadCount", 1);
        ReflectionTestUtils.setField(kafkaSenderService, "timeout", 100);

        Mockito.when(repositoryService.getAll()).thenReturn(testAlcoholList);
        Mockito.when(repositoryService.getAllWines()).thenReturn(testAlcoholList);
        Mockito.when(repositoryService.getAllSparkling()).thenReturn(testAlcoholList);

        Future<Integer> future = mock(Future.class);
        Mockito.when(future.get()).thenReturn(1);

        kafkaSendAllThreadPool = spy(Executors.newFixedThreadPool(2, kafkaSendAllThreadFactory));
        ReflectionTestUtils.setField(kafkaSenderService, "kafkaSendAllThreadPool", kafkaSendAllThreadPool);
    }

    @Test
    void sendAllAlcohols() throws InterruptedException {
        kafkaSenderService.sendAllAlcohols();
        Mockito.verify(kafkaSendAllThreadPool, times(1)).awaitTermination(100, TimeUnit.MILLISECONDS);
    }

    @Test
    void sendAllWines() throws InterruptedException {
        kafkaSenderService.sendAllWines();
        Mockito.verify(kafkaSendAllThreadPool, times(1)).awaitTermination(100, TimeUnit.MILLISECONDS);
    }

    @Test
    void sendAllSparkling() throws InterruptedException {
        kafkaSenderService.sendAllSparkling();
        Mockito.verify(kafkaSendAllThreadPool, times(1)).awaitTermination(100, TimeUnit.MILLISECONDS);
    }

    @Test
    void initPool() {
        kafkaSendAllThreadPool.shutdownNow();
        assertTrue(kafkaSendAllThreadPool.isShutdown());
        ReflectionTestUtils.invokeMethod(kafkaSenderService, "renewPools");
        ExecutorService kafkaSendAllThreadPool = (ExecutorService) ReflectionTestUtils.getField(kafkaSenderService, "kafkaSendAllThreadPool");
        assertFalse(kafkaSendAllThreadPool.isShutdown());
    }
}