package com.wine.to.up.winestyle.parser.service.controller;

import com.google.gson.Gson;
import com.wine.to.up.winestyle.parser.service.controller.exception.IllegalFieldException;
import com.wine.to.up.winestyle.parser.service.controller.exception.NoEntityException;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.MainControllerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.fail;

class MainControllerTest {
    @InjectMocks
    private MainController mainController;
    @Mock
    private MainControllerService mainControllerService;
    @Mock
    private RepositoryService alcoholRepositoryService;
    public static MockMvc mockMvc;

    private static Alcohol wine;
    private static Alcohol sparkling;
    private static final List<Alcohol> alcohol = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
        wine = Alcohol.builder()
                .id(1L).name("test_wine").type("wine").url("test_wine").imageUrl("test_wine").cropYear(1990)
                .manufacturer("test_wine").brand("test_wine").color("test_wine").country("test_wine").region("test_wine")
                .volume(1F).strength(1F).sugar("test").price(1F)
                .grape("test_wine").taste("test_wine").aroma("test_wine").foodPairing("test_wine")
                .description("test_wine").rating(1F)
                .build();
        sparkling = Alcohol.builder()
                .id(2L).name("test_sparkling").type("sparkling").url("test_sparkling").imageUrl("test_sparkling").cropYear(1990)
                .manufacturer("test_sparkling").brand("test_sparkling").color("test_sparkling").country("test_sparkling").region("test_sparkling")
                .volume(1F).strength(1F).sugar("test").price(1F)
                .grape("test_sparkling").taste("test_sparkling").aroma("test_sparkling").foodPairing("test_sparkling")
                .description("test_sparkling").rating(1F)
                .build();
    }

    @Test
    void getAlcohol() {
        String urlGet = "/winestyle/api/alcohol";
        alcohol.add(wine);
        alcohol.add(sparkling);
        Mockito.when(alcoholRepositoryService.getAll()).thenReturn(alcohol);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(urlGet).accept(MediaType.APPLICATION_JSON_VALUE);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(MockMvcResultMatchers.content().json(new Gson().toJson(Arrays.asList(wine, sparkling))))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", is("test_sparkling")));
        } catch (Exception e) {
            fail("Test failed. Can't perform get request on: " + urlGet, e);
        }
        alcohol.clear();
    }

    @Test
    void getWines() {
        String urlGet = "/winestyle/api/wines";
        alcohol.add(wine);
        Mockito.when(alcoholRepositoryService.getAllWines()).thenReturn(alcohol);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(urlGet).accept(MediaType.APPLICATION_JSON_VALUE);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(MockMvcResultMatchers.content().json(new Gson().toJson(Collections.singletonList(wine))))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].type", is("wine")));
        } catch (Exception e) {
            fail("Test failed. Can't perform get request on: " + urlGet, e);
        }
        alcohol.clear();
    }

    @Test
    void getSparkling() {
        String urlGet = "/winestyle/api/sparkling";
        alcohol.add(sparkling);
        Mockito.when(alcoholRepositoryService.getAllSparkling()).thenReturn(alcohol);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(urlGet).accept(MediaType.APPLICATION_JSON_VALUE);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(MockMvcResultMatchers.content().json(new Gson().toJson(Collections.singletonList(sparkling))))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", is("test_sparkling")))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[0].type", is("sparkling")));
        } catch (Exception e) {
            fail("Test failed. Can't perform get request on: " + urlGet, e);
        }
        alcohol.clear();
    }

    @Test
    void getAlcoholByUrl() {
        String urlGet = "/winestyle/api/alcohol/by-url?url=test_wine";
        try {
            Mockito.when(alcoholRepositoryService.getByUrl("/products/test_wine")).thenReturn(wine);
        } catch (NoEntityException e) {
            fail("Test failed. Can't get Alcohol by url.", e);
        }
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(urlGet).accept(MediaType.APPLICATION_JSON_VALUE);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(new Gson().toJson(wine)))
                    .andExpect(MockMvcResultMatchers.jsonPath("url", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("name", is("test_wine")));
        } catch (Exception e) {
            fail("Test failed. Can't perform get request on: " + urlGet, e);
        }
    }

    @Test
    void getAlcoholById() {
        String urlGet = "/winestyle/api/alcohol/1";
        try {
            Mockito.when(alcoholRepositoryService.getByID(1L)).thenReturn(wine);
        } catch (NoEntityException e) {
            fail("Test failed. Can't get Alcohol by id.", e);
        }
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(urlGet).accept(MediaType.APPLICATION_JSON_VALUE);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(MockMvcResultMatchers.content().json(new Gson().toJson(wine)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("id", is(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("name", is("test_wine")));
        } catch (Exception e) {
            fail("Test failed. Can't perform get request on: " + urlGet, e);
        }
    }

    @Test
    void getAlcoholWithFields() {
        String fieldsList = "id,name,type,url,cropYear,manufacturer,volume,strength,rating";
        String urlGet = "/winestyle/api/alcohol/with-fields/1" +
                "?fieldsList=" + fieldsList;
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("id", 1L);
        expectedResponse.put("name", "test_wine");
        expectedResponse.put("type", "wine");
        expectedResponse.put("url", "test_wine");
        expectedResponse.put("cropYear", 1990);
        expectedResponse.put("manufacturer", "test_wine");
        expectedResponse.put("volume", "test_wine");
        expectedResponse.put("strength", "test_wine");
        expectedResponse.put("rating", 1F);

        try {
            Mockito.when(mainControllerService.getAlcoholWithFields(1L, fieldsList)).thenReturn(expectedResponse);
        } catch (NoEntityException | IllegalFieldException e) {
            fail("Test failed. Can't get alcohol with fields.", e);
        }
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(urlGet).accept(MediaType.APPLICATION_JSON_VALUE);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(MockMvcResultMatchers.content().json(new Gson()
                            .toJson(expectedResponse)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("id", is(1)))
                    .andExpect(MockMvcResultMatchers.jsonPath("name", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("type", is("wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("url", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("cropYear", is(1990)))
                    .andExpect(MockMvcResultMatchers.jsonPath("manufacturer", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("volume", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("strength", is("test_wine")))
                    .andExpect(MockMvcResultMatchers.jsonPath("rating", is(1.0)));
        } catch (Exception e) {
            fail("Test failed. Can't perform get request on: " + urlGet, e);
        }
        expectedResponse.clear();
    }

    @Test
    void getAlcoholFile() {
        String urlGet = "/winestyle/api/alcohol/csv";
        Mockito.doNothing().when(mainControllerService);
        try {
            mockMvc.perform(MockMvcRequestBuilders
                    .get(urlGet)).andExpect(status().isOk());
        } catch (Exception e) {
            fail("Test failed. Cannot get response from url: " + urlGet, e);
        }
    }

    @Test
    void initProxies() {
        String urlPost = "/winestyle/api/proxy/init?maxTimeout=1";
        Mockito.doNothing().when(mainControllerService);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(urlPost);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .content().string("Proxy initialization job was successfully launched."));
        } catch (Exception e) {
            fail("Test failed! Cannot get response from " + urlPost, e);
        }
    }
}