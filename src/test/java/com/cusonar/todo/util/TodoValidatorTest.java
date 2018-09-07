package com.cusonar.todo.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.cusonar.todo.domain.Todo;

@RunWith(SpringRunner.class)
public class TodoValidatorTest {
	private Todo todo1, todo2, todo3, todo4;
	
	@Before
	public void setup() {
		todo1 = new Todo("todo1", true);
		todo2 = new Todo("todo2", true);
		todo3 = new Todo("todo3", false);
		todo4 = new Todo("todo4", false);
	}
		
	@Test
	public void isAllTest_null_일때_true() {
		List<Todo> todos = null;
		assertTrue(TodoValidator.isAll(todos, true));
		assertTrue(TodoValidator.isAll(todos, false));
	}
	
	@Test
	public void isAllTest_param이_true_일때() {
		List<Todo> todos = Arrays.asList(todo1, todo2);
		assertTrue(TodoValidator.isAll(todos, true));
		
		todos = Arrays.asList(todo1, todo2, todo3);		
		assertFalse(TodoValidator.isAll(todos, true));
	}

	@Test
	public void isAllTest_param이_false_일때() {
		List<Todo> todos = Arrays.asList(todo3, todo4);
		assertTrue(TodoValidator.isAll(todos, false));
		
		todos = Arrays.asList(todo2, todo3, todo4);		
		assertFalse(TodoValidator.isAll(todos, false));
	}
	
	@Test
	public void isCircularReferenceTest_references가_null_일때_false() {
		List<Todo> references = Arrays.asList(new Todo(1L), new Todo(2L));
		List<Todo> referencedTodos = null;
		assertFalse(TodoValidator.isCircularReference(references, referencedTodos));
	}
	
	@Test
	public void isCircularReferenceTest_referenced가_null_일때_false() {
		List<Todo> references = null;
		List<Todo> referencedTodos = Arrays.asList(new Todo(1L), new Todo(2L));
		assertFalse(TodoValidator.isCircularReference(references, referencedTodos));
	}
	
	@Test
	public void isCircularReferenceTest_null_아닐때_false() {
		List<Todo> references = Arrays.asList(new Todo(1L), new Todo(2L));
		List<Todo> referencedTodos = Arrays.asList(new Todo(3L), new Todo(4L));
		assertFalse(TodoValidator.isCircularReference(references, referencedTodos));
	}
	
	@Test
	public void isCircularReferenceTest_true() {
		List<Todo> references = Arrays.asList(new Todo(1L), new Todo(2L));
		List<Todo> referencedTodos = Arrays.asList(new Todo(2L), new Todo(3L));
		assertTrue(TodoValidator.isCircularReference(references, referencedTodos));
	}
	
	
}
