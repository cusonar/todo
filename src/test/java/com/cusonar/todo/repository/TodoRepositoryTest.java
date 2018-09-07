package com.cusonar.todo.repository;

import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.exparity.hamcrest.date.LocalDateTimeMatchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cusonar.todo.IntegrationTest;
import com.cusonar.todo.domain.Todo;

public class TodoRepositoryTest extends IntegrationTest {

	@Autowired private TodoRepository todoRepository;
	
	@Test(timeout=200)
	public void createAndUpdateTodoTest() throws InterruptedException {
		Todo newTodo = new Todo("new todo", false);
		Todo createdTodo = todoRepository.save(newTodo);
		
		assertThat(createdTodo.getCreatedDate(), LocalDateTimeMatchers.within(100, ChronoUnit.MILLIS, LocalDateTime.now()));
		assertThat(createdTodo.getLastModifiedDate(), LocalDateTimeMatchers.within(100, ChronoUnit.MILLIS, LocalDateTime.now()));
				
		Todo newTodo2 = new Todo(createdTodo.getTodoId(), "update todo", false);
		Todo updatedTodo = todoRepository.save(newTodo2);
		
		assertThat(updatedTodo.getLastModifiedDate(), LocalDateTimeMatchers.within(100, ChronoUnit.MILLIS, LocalDateTime.now()));
	}
}
