package app;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Period;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.unai.app.Application;
import com.unai.app.model.Group;
import com.unai.app.model.Person;
import com.unai.app.rest.PersonRestAPI;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes=Application.class)
public class Test1 {
	
	@Autowired
	WebApplicationContext context;
	
	@Mock
	private Person p;
	
	@Mock
	private Group g;
	
	private MockMvc mvc;
	
	@InjectMocks
	PersonRestAPI pRest;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		initPersonMock();
		initGroupMock();
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.build();
	}
	
	private void initPersonMock() {
		LocalDate b = LocalDate.parse("1964-01-12");
		when(p.getFirstName()).thenReturn("John");
		when(p.getLastName()).thenReturn("Doe");
		when(p.getBirthday()).thenReturn(b);
		when(p.getAge()).thenReturn(Period.between(b, LocalDate.now()).getYears());
	}
	
	private void initGroupMock() {
		when(g.getName()).thenReturn("A Team");
	}
	
	@Test
	public void checkJUnitWorksProperly() {
		assertNull(null);
	}
	
	@Test
	public void checkNotFound() throws Exception {
		mvc.perform(get("/people/-1"))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void checkPostThenDeletePerson() throws Exception {
		MvcResult result = mvc.perform(post("/people")
					.param("firstName", p.getFirstName())
					.param("lastName", p.getLastName())
					.param("birthday", p.getBirthday().toString()))
				.andExpect(status().isCreated())
				.andReturn();
		
		int id = getIdFromURL(result.getResponse().getRedirectedUrl());
		
		mvc.perform(get(String.format("/people/%d", id)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.firstName", is(p.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(p.getLastName())))
				.andExpect(jsonPath("$.age", is(Period.between(p.getBirthday(), LocalDate.now()).getYears())));
		
		mvc.perform(delete("/people")
					.param("id", String.valueOf(id)))
				.andExpect(status().isNoContent());
		
		mvc.perform(get(String.format("/people/%d", id)))
				.andExpect(status().isNotFound());
				
	}
	
	@Test
	public void checkPostThenDeleteGroup() throws Exception {
		MvcResult result = mvc.perform(post("/groups")
					.param("name", g.getName()))
				.andExpect(status().isCreated())
				.andReturn();
		
		int id = getIdFromURL(result.getResponse().getRedirectedUrl());
		
		mvc.perform(get(String.format("/groups/%d", id)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(g.getName())))
				.andExpect(jsonPath("$.id", is(id)));
		
		mvc.perform(delete("/groups")
					.param("id", String.valueOf(id)))
				.andExpect(status().isNoContent());
		
		mvc.perform(get(String.format("/groups/%d", id)))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void checkPostThenPutOnPerson() throws Exception {
		MvcResult result = mvc.perform(post("/people")
				.param("firstName", p.getFirstName())
				.param("lastName", p.getLastName())
				.param("birthday", p.getBirthday().toString()))
			.andExpect(status().isCreated())
			.andReturn();
		
		int id = getIdFromURL(result.getResponse().getRedirectedUrl());
		
		mvc.perform(put(String.format("/people/%d", id))
				.param("lastName", "Mockname"))
			.andExpect(status().isNoContent());
		
		mvc.perform(get(String.format("/people/%d", id)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.firstName", is(p.getFirstName())))
			.andExpect(jsonPath("$.lastName", is("Mockname")));
	}
	
	@Test
	public void checkPostThenPutOnGroup() throws Exception {
		MvcResult result = mvc.perform(post("/groups")
				.param("name", g.getName()))
			.andExpect(status().isCreated())
			.andReturn();
		
		int id = getIdFromURL(result.getResponse().getRedirectedUrl());
		
		mvc.perform(put(String.format("/groups/%d", id))
				.param("name", "Special Group"))
			.andExpect(status().isNoContent());
		
		mvc.perform(get(String.format("/groups/%d", id)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("Special Group")));
	}
	
	@Test
	public void checkAddMemberToGroup() throws Exception {
		
		MvcResult result = mvc.perform(post("/people")
				.param("firstName", p.getFirstName())
				.param("lastName", p.getLastName())
				.param("birthday", p.getBirthday().toString()))
			.andExpect(status().isCreated())
			.andReturn();
		
		int pid = getIdFromURL(result.getResponse().getRedirectedUrl());
		
		mvc.perform(get(String.format("/people/%d/group", pid)))
			.andExpect(status().isNotFound());
		
		result = mvc.perform(post("/groups")
				.param("name", g.getName()))
			.andExpect(status().isCreated())
			.andReturn();
		
		int gid = getIdFromURL(result.getResponse().getRedirectedUrl());
		
		mvc.perform(put("/groups")
				.param("pid", String.valueOf(pid))
				.param("gid", String.valueOf(gid)))
			.andExpect(status().isNoContent());
		
		mvc.perform(get(String.format("/groups/%d/members", gid)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].firstName", is(p.getFirstName())))
			.andExpect(jsonPath("$[0].id", is(pid)))
			.andExpect(jsonPath("$[0].lastName", is(p.getLastName())));
		
		mvc.perform(get(String.format("/people/%d/group", pid)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(gid)))
			.andExpect(jsonPath("$.name", is(g.getName())));
		
		mvc.perform(put("/groups")
				.param("pid", String.valueOf(pid)))
			.andExpect(status().isNoContent());
		
		mvc.perform(get(String.format("/groups/%d/members", gid)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(0)));
			
	}
	
	@Test
	public void checkNonExistingData() throws Exception {
		mvc.perform(put(String.format("/groups/%d", 212121))
				.param("name", "Mockname"))
			.andExpect(status().isNotFound());
		
		mvc.perform(put(String.format("/people/%d", 252525))
				.param("lastName", "Mockname"))
			.andExpect(status().isNotFound());
		
		mvc.perform(put("/groups")
				.param("pid", String.valueOf(54545)))
			.andExpect(status().isNotFound());
		
		MvcResult result = mvc.perform(post("/people")
				.param("firstName", p.getFirstName())
				.param("lastName", p.getLastName())
				.param("birthday", p.getBirthday().toString()))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.firstName", is(p.getFirstName())))
			.andExpect(jsonPath("$.lastName", is(p.getLastName())))
			.andReturn();
		
		int id = getIdFromURL(result.getResponse().getRedirectedUrl());
		
		mvc.perform(put("/groups")
				.param("pid", String.valueOf(id))
				.param("gid", String.valueOf(212121)))
			.andExpect(status().isNotFound());
		
		mvc.perform(delete("/groups")
				.param("id", String.valueOf(512121)))
			.andExpect(status().isNotFound());
		
		mvc.perform(delete("/people")
				.param("id", String.valueOf(212121)))
			.andExpect(status().isNotFound());
		
		mvc.perform(get(String.format("/groups/%d", 515151)))
			.andExpect(status().isNotFound());
		
		mvc.perform(get(String.format("/people/%d", 212121)))
			.andExpect(status().isNotFound());
		
		mvc.perform(get(String.format("/people/%d/group", id)))
		.andExpect(status().isNotFound());
	}
	
	private int getIdFromURL(String url) {
		String [] p = url.split("/");
		return Integer.valueOf(p[p.length - 1]);
	}
	
}
