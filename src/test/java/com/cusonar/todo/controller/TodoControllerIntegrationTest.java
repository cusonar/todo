package com.cusonar.todo.controller;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.cusonar.todo.IntegrationTest;
import com.cusonar.todo.domain.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TodoControllerIntegrationTest extends IntegrationTest {
	
	private static final String TODO_URI = "/api/todos";
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	private MockMvc mockMvc;
	@Autowired private WebApplicationContext wac;
	
	@Before
	public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        		.alwaysDo(print())
        		.build();
	}
	
	@Test
	public void _000_todo_생성테스트_todoId_포함하는_예외일때() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo(8L, "new todo", false)))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("The todoId MUST NOT be defined."));
	}

	@Test
	public void _000_todo_생성테스트_참조하는_todoId가_잘못된_예외일때() throws Exception {
		Todo ref = new Todo(Long.MAX_VALUE);
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo("new todo", false, ref)))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("Reference's todoId is wrong: " + Long.MAX_VALUE));
	}

	@Test
	public void _000_todo_생성테스트_참조하는_todo가_완료_예외일때() throws Exception {
		Todo ref = new Todo(5L, "이불빨래", true);
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo("new todo", false, ref)))
			.accept(MediaType.APPLICATION_JSON))
		.andDo(print())
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("All of references MUST NOT be completed."));
	}

	@Test
	public void _000_todo_리스트조회테스트_파라미터가_소숫점_예외일때() throws Exception {
		float page = 0.5f;
		int size = 2;
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?page=" + page + "&size=" + size).accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("The parameters is wrong."));
	}

	@Test
	public void _000_todo_참조업데이트_순환참조_예외일때() throws Exception {
		Todo newTodo = new Todo(1L, "집안일", false, new Todo(2L));
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(objectToJsonString(newTodo))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.errorCode").value(400))
				.andExpect(jsonPath("$.message").value("Referenced todo CAN NOT refer(Circular Reference)"));
	}

	@Test
	public void _000_todo_업데이트테스트_ID가_조회가_되지_않는_예외일때() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo(Long.MAX_VALUE, "new todo", false)))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(404))
			.andExpect(jsonPath("$.message").value("ID: " + Long.MAX_VALUE));
	}

	@Test
	public void _000_todo_완료업데이트테스트_참조된_todo가_미완료_예외일때() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo(1L, "new todo", true)))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("All of referenced todos MUST BE completed."));
	}

	@Test
	public void _000_todo_미완료업데이트테스트_참조하는_todo가_완료_예외일때() throws Exception {
		Todo todo2 = new Todo(2L);
		Todo todo5 = new Todo(5L);
		Todo newTodo = new Todo(6L, "new todo", false, todo2, todo5);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("All of references MUST NOT BE completed."));
	}

	@Test
	public void _000_todo_리스트조회테스트() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?page=1&size=2").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.todos", hasSize(2)));
	}

	@Test
	public void _000_todo_리스트조회테스트_page디폴트일때() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?size=2").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.todos", hasSize(2)));
	}

	@Test
	public void _000_todo_리스트조회테스트_size디폴트일때() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?page=1").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.todos", hasSize(3)));
	}

	@Test
	public void _000_todo_리스트조회테스트_last_modified_캐시일때() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
		String lastModified = result.getResponse().getHeader("Last-Modified");
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI)
			.header("If-Modified-Since", lastModified).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotModified());
	}

	@Test
	public void _001_todo_생성테스트_레퍼런스_포함() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo("new todo", false)))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.todoId").exists())
			.andExpect(jsonPath("$.createdDate").exists())
			.andExpect(jsonPath("$.lastModifiedDate").exists())
			.andExpect(jsonPath("$.title").value("new todo"))
			.andExpect(jsonPath("$.completed").value(false));
	}
	
	@Test
	public void _002_todo_생성테스트_레퍼런스_미포함() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo("new todo", false)))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.todoId").exists())
			.andExpect(jsonPath("$.createdDate").exists())
			.andExpect(jsonPath("$.lastModifiedDate").exists())
			.andExpect(jsonPath("$.title").value("new todo"))
			.andExpect(jsonPath("$.completed").value(false));
	}
	
	@Test
	public void _003_todo_업데이트테스트_타이틀수정() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(new Todo(1L, "집 안 일", false)))
	        .accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.todoId").value(1))
			.andExpect(jsonPath("$.title").value("집 안 일"))
			.andExpect(jsonPath("$.completed").value(false));
	}
	
	@Test
	public void _004_todo_업데이트테스트_완료수정() throws Exception {
		Todo newTodo = new Todo(7L, "거실청소", true);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.completed").value(true))
			.andExpect(jsonPath("$.lastModifiedDate").exists());
	}
	
	@Test
	public void _005_todo_업데이트테스트_미완료수정() throws Exception {
		Todo newTodo = new Todo(8L, "화장실청소", false);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.completed").value(false))
			.andExpect(jsonPath("$.lastModifiedDate").exists());
	}
	
	@Test
	public void _006_todo_참조업데이트테스트() throws Exception {
		Todo newTodo = new Todo(5L, "이불빨래", true, new Todo(1L), new Todo(2L));
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(objectToJsonString(newTodo))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.references[0].todoId").value(1))
				.andExpect(jsonPath("$.references[1].todoId").value(2));
	}
	
	private String objectToJsonString(Object object) throws Exception {
		return jsonMapper.writeValueAsString(object);
	}
}
