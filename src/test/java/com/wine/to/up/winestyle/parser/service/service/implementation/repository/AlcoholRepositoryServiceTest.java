package com.wine.to.up.winestyle.parser.service.service.implementation.repository;

import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.repository.AlcoholRepository;
import com.wine.to.up.winestyle.parser.service.repository.ErrorOnSavingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AlcoholRepositoryServiceTest {
    @InjectMocks
    private AlcoholRepositoryService alcoholRepositoryService;
    @Mock
    private AlcoholRepository alcoholRepository;
    @Mock
    private ErrorOnSavingRepository errorOnSavingRepository;

    private Alcohol alcohol;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
        alcohol = new Alcohol();
        alcohol.setPrice(1F);
        alcohol.setRating(2F);
    }

    @Test
    public void updatePrice() throws NoEntityException {
        Mockito.when(alcoholRepository.findByUrl("test"))
                .thenReturn(java.util.Optional.ofNullable(alcohol));
        Mockito.when(alcoholRepository.save(alcohol)).thenReturn(alcohol);

        alcoholRepositoryService.updatePrice(1F, "test");
        Mockito.verify(alcoholRepository, Mockito.times(1)).save(alcohol);
    }

    @Test
    public void updateRating() throws NoEntityException {
        Mockito.when(alcoholRepository.findByUrl("test"))
                .thenReturn(java.util.Optional.ofNullable(alcohol));
        Mockito.when(alcoholRepository.save(alcohol)).thenReturn(alcohol);

        alcoholRepositoryService.updateRating(1F, "test");
        Mockito.verify(alcoholRepository, Mockito.times(1)).save(alcohol);
    }

    @Test
    void getAll() {
        Mockito.when(alcoholRepository.findAll()).thenReturn(List.of(alcohol));
        assertEquals(List.of(alcohol), alcoholRepositoryService.getAll());
        Mockito.verify(alcoholRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getAllWines() {
        Mockito.when(alcoholRepository.findAllWines()).thenReturn(List.of(alcohol));
        assertEquals(List.of(alcohol), alcoholRepositoryService.getAllWines());
        Mockito.verify(alcoholRepository, Mockito.times(1)).findAllWines();
    }

    @Test
    void getAllSparkling() {
        Mockito.when(alcoholRepository.findAllSparkling()).thenReturn(List.of(alcohol));
        assertEquals(List.of(alcohol), alcoholRepositoryService.getAllSparkling());
        Mockito.verify(alcoholRepository, Mockito.times(1)).findAllSparkling();
    }

    @Test
    void getByUrl() throws NoEntityException {
        Mockito.when(alcoholRepository.findByUrl("test")).thenReturn(java.util.Optional.ofNullable(alcohol));
        assertEquals(alcohol, alcoholRepositoryService.getByUrl("test"));
    }

    @Test
    void getByUrlException() {
        Mockito.when(alcoholRepository.findByUrl("test")).thenReturn(Optional.empty());
        try {
            alcoholRepositoryService.getByUrl("test");
            fail("Test failed! NoEntityException not throwed.");
        } catch (NoEntityException e) {
            assertEquals(NoEntityException.class, e.getClass());
        }
    }

    @Test
    void add() {
        Mockito.when(alcoholRepository.save(alcohol)).thenReturn(alcohol);
        alcoholRepositoryService.add(alcohol);
        alcoholRepositoryService.add(alcohol);
        Mockito.verify(alcoholRepository, Mockito.times(2)).save(alcohol);
    }

    @Test
    void getByID() throws NoEntityException {
        Mockito.when(alcoholRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(alcohol));
        assertEquals(alcohol, alcoholRepositoryService.getByID(1L));
    }
}