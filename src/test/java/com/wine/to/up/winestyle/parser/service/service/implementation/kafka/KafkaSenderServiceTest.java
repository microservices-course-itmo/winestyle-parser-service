package com.wine.to.up.winestyle.parser.service.service.implementation.kafka;

import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.*;

import static org.mockito.Mockito.*;

class KafkaSenderServiceTest {
    @Mock
    RepositoryService repositoryService;
    @Mock
    KafkaSender kafkaSender;
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

        ReflectionTestUtils.setField(kafkaSenderService, "repositoryService", repositoryService);
        ReflectionTestUtils.setField(kafkaSenderService, "kafkaSender", kafkaSender);

        Mockito.when(repositoryService.getAll()).thenReturn(testAlcoholList);
        Mockito.when(repositoryService.getAllWines()).thenReturn(testAlcoholList);
        Mockito.when(repositoryService.getAllSparkling()).thenReturn(testAlcoholList);
        Mockito.when(kafkaSender.sendAlcoholToKafka(testAlcohol)).thenReturn(1);

        Future<Integer> future = mock(Future.class);
        Mockito.when(future.get()).thenReturn(1);

    }

    @Test
    void sendAllAlcohol() {
        kafkaSenderService.sendAllAlcohol();
        Mockito.verify(repositoryService, times(1)).getAll();
        }

    @Test
    void sendAllWines() {
        kafkaSenderService.sendAllWines();
        Mockito.verify(repositoryService, times(1)).getAllWines();
    }

    @Test
    void sendAllSparkling() {
        kafkaSenderService.sendAllSparkling();
        Mockito.verify(repositoryService, times(1)).getAllSparkling();
    }
}