package com.cusonar.todo.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import com.cusonar.todo.domain.Todo;
import com.cusonar.todo.repository.TodoRepository;

@RunWith(SpringRunner.class)
public class TodoServiceTest {
	
	private static final String TODO_ID = "todoId";

	@Mock
	private TodoRepository todoRepository;
	
	@InjectMocks
	private TodoServiceImpl todoService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void saveTodoTest(){
		Todo expected = new Todo("new todo", true);
		when(todoRepository.save(expected)).thenReturn(expected);
		Todo actual = todoService.save(expected);
		validate(expected, actual);
	}
	
	@Test
	public void findByIdTodoTest(){
		Todo expected = new Todo(1L, "todo1", true);
		when(todoRepository.findById(1L)).thenReturn(Optional.of(expected));
		Todo actual = todoService.findById(1L).get();
		validate(expected, actual);
	}
	
	@Test
	public void findAllTodoTest(){
		int page = 1, size = 3;
		Todo todo1 = new Todo(1L, "todo1", false);
		Todo todo2 = new Todo(2L, "todo 2", false);
		Todo todo3 = new Todo(3L, "todo 3", true, todo1);
				
		List<Todo> expected = Arrays.asList(todo1, todo2, todo3);
		PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.ASC, TODO_ID);
		Page<Todo> todos = new PageImpl<>(expected);
		when(todoRepository.findAll(request)).thenReturn(todos);
		
		List<Todo> actual = todoService.findAll(page, size);
		assertEquals(size, actual.size());
		for (int i=0; i<size; ++i) {
			validate(expected.get(i), actual.get(i));
		}
	}
	
	@Test
	public void countTodoTest() {
		long expected = 3L;
		when(todoRepository.count()).thenReturn(expected);
		long actual = todoService.count();
		assertEquals(expected, actual);
	}
	
	@Test
	public void findByReferencesInTest() {
		Todo todo1 = new Todo(1L, "todo1", false);
		Todo todo2 = new Todo(2L, "todo2", false, todo1);
		Todo todo3 = new Todo(3L, "todo3", false, todo1);
		List<Todo> references = Arrays.asList(todo1);
		List<Todo> expected = Arrays.asList(todo2, todo3);
		when(todoRepository.findByReferencesIn(references)).thenReturn(expected);
		List<Todo> actual = todoService.findByReferencesIn(references);
		validate(expected, actual);
	}
	
	@Test
	public void getResourceLastModifiedTest() {
		LocalDateTime now = LocalDateTime.now();
		when(todoRepository.getMaxLastModifiedDate()).thenReturn(now);
		ZonedDateTime zdt = ZonedDateTime.of(now, ZoneId.of("GMT"));
		long expected = zdt.toInstant().toEpochMilli();
		long actual = todoService.getResourceLastModified();
		assertEquals(expected, actual);
		
	}
	
	private void validate(Todo expected, Todo actual) {
		assertEquals(expected.getTodoId(), actual.getTodoId());
		assertEquals(expected.getTitle(), actual.getTitle());
		assertEquals(expected.isCompleted(), actual.isCompleted());
	}
	
	private void validate(List<Todo> expected, List<Todo> actual) {
		if (expected != null && actual != null) {
			assertEquals(expected.size(), actual.size());
			for (int i=0; i<expected.size(); ++i) {
				validate(expected.get(i), actual.get(i));
			}
		}
	}
}
