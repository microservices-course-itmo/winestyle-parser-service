package com.wine.to.up.winestyle.parser.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.wine.to.up.commonlib.metrics.CommonMetricsCollector;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.RepositoryService;
import com.wine.to.up.winestyle.parser.service.service.implementation.controller.ParsingControllerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RepositoryService repositoryService;

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
 
        @Bean
        public CommonMetricsCollector metricsCollector() {
            return Mockito.mock(CommonMetricsCollector.class);
		}
		
		@Bean
		public ParsingControllerService parsingControllerService() {
			return Mockito.mock(ParsingControllerService.class);
		}
    }

	@BeforeEach
    public void setUp() {
		if (repositoryService.getAll().isEmpty()) {
			Alcohol alco = Alcohol.builder().brand("testBrand").aroma("strong")
					.color("white").cropYear(1888).description("full")
					.foodPairing("-").country("Italy").grape("Banana")
					.imageUrl("ololo.jpg").url("/products/hello-wine.html")
					.region("Perm'").volume(0.75f).strength(1.0f)
					.price(1234f).taste("delicious").manufacturer("my grandma")
					.sugar("Sugar").name("Vinishko").type("Вино").build();
			repositoryService.add(alco);
		}
	}

	@Test
	public void alcoholByIDSuccess() throws Exception {
		int id = 1;
		Alcohol alcohol = repositoryService.getByID(id);
		mockMvc.perform(get("/winestyle/api/alcohol/" + id))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(alcohol)));
	}

	@Test
	public void alcoholByURLSuccess() throws Exception {
		String url = "hello-wine.html";
		Alcohol alcohol = repositoryService.getByUrl("/products/" + url);
		mockMvc.perform(get("/winestyle/api/alcohol/by-url/" + url))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(alcohol)));
	}

	@Test
	public void alcoholByIDFail() throws Exception {
		int id = 12434;
		mockMvc.perform(get("/winestyle/api/alcohol/" + id))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void alcoholByURLSFail() throws Exception {
		String url = "oh-url-notexisting";
		mockMvc.perform(get("/winestyle/api/alcohol/by-url/" + url))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	public void getAllWines() throws Exception {
		List<Alcohol> wines = repositoryService.getAllWines();
		log.info(wines.toString());
		mockMvc.perform(get("/winestyle/api/wines"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(wines)));
	}

	@Test
	public void getAllSparkling() throws Exception {
		List<Alcohol> sparkling = repositoryService.getAllSparkling();
		mockMvc.perform(get("/winestyle/api/sparkling"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(sparkling)));
	}

	@Test
	public void alcoholWithFieldsSuccess() throws Exception {
		int id = 1;
		String fields = "name,cropYear";
		Alcohol alcohol = repositoryService.getByID(id);
		Map<String, Object> alco = new HashMap<>();
		alco.put("name", alcohol.getName());
		alco.put("cropYear", alcohol.getCropYear());
		mockMvc.perform(get("/winestyle/api/alcohol/with-fields/" + id + "?fieldsList=" + fields))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(alco)));
	}

	@Test
	public void alcoholWithFieldsFailByID() throws Exception {
		int id = 12434;
		String fields = "name,cropYear";
		mockMvc.perform(get("/winestyle/api/alcohol/with-fields/" + id + "?fieldsList=" + fields))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
}