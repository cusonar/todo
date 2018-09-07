package com.cusonar.todo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.cusonar.todo.domain.Todo;
import com.cusonar.todo.domain.TodoResponse;
import com.cusonar.todo.exception.TodoException;
import com.cusonar.todo.exception.TodoNotFoundException;
import com.cusonar.todo.exception.TodoReferenceException;
import com.cusonar.todo.service.TodoService;
import com.cusonar.todo.util.TodoValidator;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/todos")
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200"})
public class TodoRestController {
	
	@Autowired private TodoService todoService;
	
	@GetMapping("")
	public ResponseEntity<TodoResponse> readList(
			@RequestParam(defaultValue="1") Integer page,
			@RequestParam(defaultValue="3") Integer size,
			WebRequest swr
			) throws TodoException {
		log.info("todo's readList: page: " + page + ", size: " + size);
		
		if (swr.checkNotModified(todoService.getResourceLastModified())) {
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
		}
		
		if (page < 1 || size < 1) {
			throw new TodoException("The parameters is wrong.");
		}
		TodoResponse res = new TodoResponse();
		res.setTotalCount(todoService.count());
		res.setTodos(todoService.findAll(page, size));
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@PostMapping("")
	public ResponseEntity<Todo> create(@RequestBody Todo newTodo) throws TodoException {
		log.info("todo create: " + newTodo);
		if (newTodo.getTodoId() != null) {
			throw new TodoException("The todoId MUST NOT be defined.");
		}
		List<Todo> references = getReferencesByIds(newTodo);
		if (!newTodo.isCompleted() && !TodoValidator.isAll(references, false)) {
			throw new TodoReferenceException("All of references MUST NOT be completed.");
		}
		Todo result = todoService.save(newTodo);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}
	
	@PatchMapping("")
	public ResponseEntity<Todo> update(@RequestBody Todo newTodo) throws TodoException {
		log.info("todo update: " + newTodo);
		Optional<Todo> oldTodo = todoService.findById(newTodo.getTodoId());
		if (!oldTodo.isPresent()) {
			throw new TodoNotFoundException("ID: " + newTodo.getTodoId());
		}

		List<Todo> references = getReferencesByIds(newTodo);
		if (!newTodo.isCompleted() && !TodoValidator.isAll(references, false)) {
			throw new TodoReferenceException("All of references MUST NOT BE completed.");
		}
		
		List<Todo> referencedTodos = todoService.findByReferencesIn(Arrays.asList(oldTodo.get()));
		if (TodoValidator.isCircularReference(newTodo.getReferences(), referencedTodos)) {
			throw new TodoReferenceException("Referenced todo CAN NOT refer(Circular Reference)");
		}
		if (newTodo.isCompleted() && !TodoValidator.isAll(referencedTodos, true)) {
			throw new TodoReferenceException("All of referenced todos MUST BE completed.");
		}
		
		
		return new ResponseEntity<>(todoService.save(newTodo), HttpStatus.OK);
	}
	
	private List<Todo> getReferencesByIds(Todo todo) throws TodoReferenceException {
		List<Todo> references = new ArrayList<>();
		List<Todo> todoIdList = todo.getReferences();
		if (todoIdList == null) return references;
		for (Todo reference : todoIdList) {
			Optional<Todo> refTodo = todoService.findById(reference.getTodoId());
			if (!refTodo.isPresent()) {
				throw new TodoReferenceException("Reference's todoId is wrong: " + reference.getTodoId());
			}
			references.add(refTodo.get());
		}
		return references;
	}
}
