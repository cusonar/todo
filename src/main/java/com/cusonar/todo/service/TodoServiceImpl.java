package com.cusonar.todo.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cusonar.todo.domain.Todo;
import com.cusonar.todo.repository.TodoRepository;

@Service
public class TodoServiceImpl implements TodoService {
	
	@Autowired private TodoRepository todoRepository;
	private static final String TODO_ID = "todoId";
	private static final String ZONE_ID = "GMT";
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public Todo save(Todo todo) {
		return todoRepository.save(todo);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Optional<Todo> findById(long id) {
		return todoRepository.findById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Todo> findAll(int page, int size) {
		PageRequest request = PageRequest.of(page - 1, size, Sort.Direction.ASC, TODO_ID);
		Page<Todo> result = todoRepository.findAll(request);
		return result.getContent();
	}

	@Override
	@Transactional(readOnly=true)
	public long count() {
		return todoRepository.count();
	}

	@Override
	@Transactional(readOnly=true)
	public List<Todo> findByReferencesIn(List<Todo> references) {
		return todoRepository.findByReferencesIn(references);
	}
	
	@Override
	@Transactional(readOnly=true)
	public long getResourceLastModified() {
		ZonedDateTime zdt = ZonedDateTime.of(todoRepository.getMaxLastModifiedDate(), ZoneId.of(ZONE_ID));
		return zdt.toInstant().toEpochMilli();
	}

}
