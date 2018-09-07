package com.cusonar.todo.service;

import java.util.List;
import java.util.Optional;

import com.cusonar.todo.domain.Todo;

public interface TodoService {

	public Todo save(Todo todo);
	public Optional<Todo> findById(long id);
	public List<Todo> findAll(int page, int size);
	public long count();
	public List<Todo> findByReferencesIn(List<Todo> references);
	public long getResourceLastModified();
}
