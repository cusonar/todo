package com.cusonar.todo.controller;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.cusonar.todo.aop.ExceptionController;
import com.cusonar.todo.domain.Todo;
import com.cusonar.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
public class TodoControllerTest {
	
	private static final String TODO_URI = "/api/todos";
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	@Mock
    private TodoService todoService;
	
    @InjectMocks
    private TodoRestController todoRestController;
    
    private MockMvc mockMvc;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(todoRestController)
				.setControllerAdvice(new ExceptionController())
				.alwaysDo(print())
				.build();
	}
	
	@Test
	public void todo_생성테스트_레퍼런스_포함() throws Exception {
		Todo ref = new Todo(2L);
		Todo newTodo = new Todo("new todo", false, ref);
		Todo createdTodo = new Todo(1L, "new todo", false, ref);
		when(todoService.save(newTodo)).thenReturn(createdTodo);
		when(todoService.findById(2L)).thenReturn(Optional.of(ref));
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.todoId").value(1))
			.andExpect(jsonPath("$.title").value("new todo"))
			.andExpect(jsonPath("$.completed").value(false))
			.andExpect(jsonPath("$.references", hasSize(1)));
	}
	
	@Test
	public void todo_생성테스트_레퍼런스_미포함() throws Exception {
		Todo newTodo = new Todo("new todo", false);
		Todo createdTodo = new Todo(1L, "new todo", false);
		when(todoService.save(newTodo)).thenReturn(createdTodo);
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.todoId").value(1))
			.andExpect(jsonPath("$.title").value("new todo"))
			.andExpect(jsonPath("$.completed").value(false));
	}
	
	@Test
	public void todo_생성테스트_todoId_포함하는_예외일때() throws Exception {
		Todo newTodo = new Todo(8L, "new todo", false);
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("The todoId MUST NOT be defined."));
	}
	
	@Test
	public void todo_생성테스트_참조하는_todoId가_잘못된_예외일때() throws Exception {
		Todo ref = new Todo(Long.MAX_VALUE);
		Todo newTodo = new Todo("new todo", false, ref);
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("Reference's todoId is wrong: " + Long.MAX_VALUE));
	}
	
	@Test
	public void todo_생성테스트_참조하는_todo가_완료_예외일때() throws Exception {
		Todo ref5 = new Todo(5L, "ref todo", true);
		Todo ref6 = new Todo(6L, "ref todo2", false);
		Todo newTodo = new Todo("new todo", false, ref5, ref6);
		when(todoService.findById(5L)).thenReturn(Optional.of(ref5));
		when(todoService.findById(6L)).thenReturn(Optional.of(ref6));
		mockMvc.perform(MockMvcRequestBuilders.post(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("All of references MUST NOT be completed."));
	}
	
	@Test
	public void todo_리스트조회테스트() throws Exception {
		Todo todo1 = new Todo("todo1", false);
		Todo todo2 = new Todo("todo2", false);
		Todo todo3 = new Todo("todo3", false);
		when(todoService.count()).thenReturn(3L);
		when(todoService.findAll(1, 3)).thenReturn(Arrays.asList(todo1, todo2, todo3));
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI)
		        .contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalCount").value(3))
				.andExpect(jsonPath("$.todos", hasSize(3)));
	}
	
	@Test
	public void todo_리스트조회테스트_page디폴트일때() throws Exception {
		Todo todo1 = new Todo("todo1", false);
		Todo todo2 = new Todo("todo2", false);
		when(todoService.count()).thenReturn(3L);
		when(todoService.findAll(1, 2)).thenReturn(Arrays.asList(todo1, todo2));
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?size=2").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.todos", hasSize(2)));
	}
	
	@Test
	public void todo_리스트조회테스트_size디폴트일때() throws Exception {
		Todo todo1 = new Todo("todo1", false);
		Todo todo2 = new Todo("todo2", false);
		Todo todo3 = new Todo("todo3", false);
		when(todoService.count()).thenReturn(3L);
		when(todoService.findAll(2, 3)).thenReturn(Arrays.asList(todo1, todo2, todo3));
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?page=2").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.todos", hasSize(3)));
	}
	
	@Test
	public void todo_리스트조회테스트_last_modified_캐시일때() throws Exception {
		when(todoService.getResourceLastModified()).thenReturn(System.currentTimeMillis());
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn();
		String lastModified = result.getResponse().getHeader("Last-Modified");
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI)
			.header("If-Modified-Since", lastModified).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotModified());
	}
	
	@Test
	public void todo_리스트조회테스트_파라미터가_page가_음수_예외일때() throws Exception {
		int page = -1, size = 2;
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?page=" + page + "&size=" + size).accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("The parameters is wrong."));
	}
	
	@Test
	public void todo_리스트조회테스트_파라미터가_size가_음수_예외일때() throws Exception {
		int page = 1, size = -2;
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?page=" + page + "&size=" + size).accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("The parameters is wrong."));
	}
	
	@Test
	public void todo_리스트조회테스트_파라미터가_소숫점_예외일때() throws Exception {
		float page = 0.5f;
		int size = 2;
		mockMvc.perform(MockMvcRequestBuilders.get(TODO_URI + "?page=" + page + "&size=" + size).accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("The parameters is wrong."));
	}
	
	@Test
	public void todo_업데이트테스트_타이틀수정() throws Exception {
		Todo newTodo = new Todo(1L, "new todo", false);
		Todo referencedTodo = new Todo(3L, "referenced todo", false);
		Todo updatedTodo = new Todo(1L, "updated todo", false);
		when(todoService.findById(1L)).thenReturn(Optional.of(newTodo));
		when(todoService.findByReferencesIn(Arrays.asList(newTodo))).thenReturn(Arrays.asList(referencedTodo));
		when(todoService.save(newTodo)).thenReturn(updatedTodo);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
	        .accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.todoId").value(1))
			.andExpect(jsonPath("$.title").value("updated todo"))
			.andExpect(jsonPath("$.completed").value(false));
	}
	
	@Test
	public void todo_업데이트테스트_완료수정() throws Exception {
		Todo oldTodo = new Todo(1L, "old todo", false);
		Todo newTodo = new Todo(1L, "new todo", true);
		Todo referencedTodo2 = new Todo(2L, "ref2", true, oldTodo);
		Todo referencedTodo3 = new Todo(3L, "ref3", true, oldTodo);
		when(todoService.findById(1L)).thenReturn(Optional.of(oldTodo));
		when(todoService.findByReferencesIn(Arrays.asList(oldTodo))).thenReturn(Arrays.asList(referencedTodo2, referencedTodo3));
		when(todoService.save(newTodo)).thenReturn(newTodo);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.completed").value(true));
	}
	
	@Test
	public void todo_업데이트테스트_미완료수정() throws Exception {
		Todo ref2 = new Todo(2L, "ref2", false);
		Todo ref3 = new Todo(3L, "ref3", false);
		Todo oldTodo = new Todo(1L, "old todo", true, ref2, ref3);
		Todo newTodo = new Todo(1L, "new todo", false, ref2, ref3);
		when(todoService.findById(1L)).thenReturn(Optional.of(oldTodo));
		when(todoService.findById(2L)).thenReturn(Optional.of(ref2));
		when(todoService.findById(3L)).thenReturn(Optional.of(ref3));
		when(todoService.save(newTodo)).thenReturn(newTodo);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.completed").value(false));
	}
	
	@Test
	public void todo_참조업데이트테스트() throws Exception {
		Todo ref2 = new Todo(2L, "ref2", false);
		Todo ref3 = new Todo(3L, "ref3", false);
		Todo ref4 = new Todo(4L, "ref4", false);
		Todo oldTodo = new Todo(1L, "old todo", true, ref2, ref3);
		Todo newTodo = new Todo(1L, "new todo", false, ref3, ref4);
		when(todoService.findById(1L)).thenReturn(Optional.of(oldTodo));
		when(todoService.findById(2L)).thenReturn(Optional.of(ref2));
		when(todoService.findById(3L)).thenReturn(Optional.of(ref3));
		when(todoService.findById(4L)).thenReturn(Optional.of(ref4));
		when(todoService.save(newTodo)).thenReturn(newTodo);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(objectToJsonString(newTodo))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.references[0].todoId").value(3))
				.andExpect(jsonPath("$.references[1].todoId").value(4));
	}
	
	@Test
	public void todo_참조업데이트_순환참조_예외일때() throws Exception {
		Todo ref = new Todo(2L, "ref", false);
		Todo todo = new Todo(1L, "todo", false, ref);
		Todo newRef = new Todo(2L, "ref", false, todo);
		when(todoService.findById(1L)).thenReturn(Optional.of(todo));
		when(todoService.findById(2L)).thenReturn(Optional.of(ref));
		when(todoService.findByReferencesIn(Arrays.asList(ref))).thenReturn(Arrays.asList(todo));
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(objectToJsonString(newRef))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.errorCode").value(400))
				.andExpect(jsonPath("$.message").value("Referenced todo CAN NOT refer(Circular Reference)"));
	}
	
	@Test
	public void todo_참조업데이트테스트_ID가_조회가_되지_않는_예외일때() throws Exception {
		Todo newTodo = new Todo(Long.MAX_VALUE, "new todo", false);
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(404))
			.andExpect(jsonPath("$.message").value("ID: " + Long.MAX_VALUE));
	}
	
	@Test
	public void todo_완료업데이트테스트_참조된_todo가_미완료_예외일때() throws Exception {
		Todo oldTodo = new Todo(1L, "old todo", false);
		Todo newTodo = new Todo(1L, "new todo", true);
		Todo referencedTodo2 = new Todo(2L, "ref2", true, oldTodo);
		Todo referencedTodo3 = new Todo(3L, "ref3", false, oldTodo);
		when(todoService.findById(1L)).thenReturn(Optional.of(oldTodo));
		when(todoService.findByReferencesIn(Arrays.asList(oldTodo))).thenReturn(Arrays.asList(referencedTodo2, referencedTodo3));
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("All of referenced todos MUST BE completed."));
	}
	
	@Test
	public void todo_미완료업데이트테스트_참조하는_todo가_완료_예외일때() throws Exception {
		Todo ref2 = new Todo(2L, "ref2", false), ref3 = new Todo(3L, "ref3", true);
		Todo oldTodo = new Todo(1L, "old todo", true, ref2, ref3);
		Todo newTodo = new Todo(1L, "new todo", false, ref2, ref3);
		when(todoService.findById(1L)).thenReturn(Optional.of(oldTodo));
		when(todoService.findById(2L)).thenReturn(Optional.of(ref2));
		when(todoService.findById(3L)).thenReturn(Optional.of(ref3));
		mockMvc.perform(MockMvcRequestBuilders.patch(TODO_URI)
	        .contentType(MediaType.APPLICATION_JSON)
	        .content(objectToJsonString(newTodo))
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.errorCode").value(400))
			.andExpect(jsonPath("$.message").value("All of references MUST NOT BE completed."));
	}
	
	private String objectToJsonString(Object object) throws Exception {
		return jsonMapper.writeValueAsString(object);
	}
}
