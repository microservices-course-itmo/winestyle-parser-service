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

class ApplicationRepositoryServiceTest {
    @InjectMocks
    private ApplicationRepositoryService ApplicationRepositoryService;
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
    void getAll() {
        Mockito.when(alcoholRepository.findAll()).thenReturn(List.of(alcohol));
        assertEquals(List.of(alcohol), ApplicationRepositoryService.getAll());
        Mockito.verify(alcoholRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getAllWines() {
        Mockito.when(alcoholRepository.findAllWines()).thenReturn(List.of(alcohol));
        assertEquals(List.of(alcohol), ApplicationRepositoryService.getAllWines());
        Mockito.verify(alcoholRepository, Mockito.times(1)).findAllWines();
    }

    @Test
    void getAllSparkling() {
        Mockito.when(alcoholRepository.findAllSparkling()).thenReturn(List.of(alcohol));
        assertEquals(List.of(alcohol), ApplicationRepositoryService.getAllSparkling());
        Mockito.verify(alcoholRepository, Mockito.times(1)).findAllSparkling();
    }

    @Test
    void getByUrl() throws NoEntityException {
        Mockito.when(alcoholRepository.findByUrl("test")).thenReturn(java.util.Optional.ofNullable(alcohol));
        assertEquals(alcohol, ApplicationRepositoryService.getByUrl("test"));
    }

    @Test
    void getByUrlException() {
        Mockito.when(alcoholRepository.findByUrl("test")).thenReturn(Optional.empty());
        try {
            ApplicationRepositoryService.getByUrl("test");
            fail("Test failed! NoEntityException not throwed.");
        } catch (NoEntityException e) {
            assertEquals(NoEntityException.class, e.getClass());
        }
    }

    @Test
    void add() {
        Mockito.when(alcoholRepository.save(alcohol)).thenReturn(alcohol);
        ApplicationRepositoryService.add(alcohol);
        ApplicationRepositoryService.add(alcohol);
        Mockito.verify(alcoholRepository, Mockito.times(2)).save(alcohol);
    }

    @Test
    void getByID() throws NoEntityException {
        Mockito.when(alcoholRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(alcohol));
        assertEquals(alcohol, ApplicationRepositoryService.getByID(1L));
    }
}