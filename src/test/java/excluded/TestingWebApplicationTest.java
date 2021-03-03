package excluded;

import com.google.gson.Gson;
import com.wine.to.up.winestyle.parser.service.domain.entity.Alcohol;
import com.wine.to.up.winestyle.parser.service.service.implementation.repository.ApplicationRepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ComponentScan("com.wine.to.up")
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class TestingWebApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ApplicationRepositoryService applicationRepositoryService;

	@Test
	public void alcoholByIDSuccess() throws Exception {
		int id = 1;
		Alcohol alcohol = applicationRepositoryService.getByID(id);
		mockMvc.perform(get("/winestyle/api/alcohol/" + id))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(alcohol)));
	}

	// @Test
	// public void alcoholByURLSuccess() throws Exception {
	// 	String url = "Chateau-Les-Jouberts-Cuvee-Prestige-Blaye-Cotes-de-Bordeaux-AOC-2014.html";
	// 	Alcohol alcohol = alcoholRepositoryService.getByUrl(url);
	// 	mockMvc.perform(get("/winestyle/api/alcohol/by-url/" + url))
	// 			.andDo(print())
	// 			.andExpect(status().isOk())
	// 			.andExpect(content().json(new Gson().toJson(alcohol)));
	// }

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
		List<Alcohol> wines = applicationRepositoryService.getAllWines();
		mockMvc.perform(get("/winestyle/api/wines"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(wines)));
	}

	@Test
	public void getAllSparkling() throws Exception {
		List<Alcohol> sparkling = applicationRepositoryService.getAllSparkling();
		mockMvc.perform(get("/winestyle/api/sparkling"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(sparkling)));
	}

	@Test
	public void alcoholWithFieldsSuccess() throws Exception {
		int id = 1;
		String fields = "name,cropYear";
		Alcohol alcohol = applicationRepositoryService.getByID(id);
		Map<String, Object> alco = new HashMap<>();
		alco.put("name", alcohol.getName());
		alco.put("cropYear", alcohol.getCropYear());
		mockMvc.perform(get("/winestyle/api/alcohol/with-fields/" + id + "?fieldsList=" + fields))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().json(new Gson().toJson(alco)));
	}

	@Test
	public void alcoholWithFieldsFailID() throws Exception {
		int id = 12434;
		String fields = "name,year";
		mockMvc.perform(get("/winestyle/api/alcohol/with-fields/" + id + "?fieldsList=" + fields))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
}