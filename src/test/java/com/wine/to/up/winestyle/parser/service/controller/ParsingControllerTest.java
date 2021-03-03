package com.wine.to.up.winestyle.parser.service.controller;

import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ParsingControllerTest {
    @InjectMocks
    private ParsingController parsingController;
    @Mock
    private ParsingControllerService parsingControllerService;

    public static MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(parsingController).build();
    }

    @Test
    void startParsing() {
        String urlPost = "/winestyle/api/parse/SPB/WINE";
        Mockito.doNothing().when(parsingControllerService);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(urlPost);
        try {
            mockMvc.perform(mockRequest)
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers
                            .content().string("Parsing job was successfully launched."));
        } catch (Exception e) {
            fail("Test failed! Cannot get response from " + urlPost, e);
        }
    }
}