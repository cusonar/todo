package com.cusonar.todo.util;

import java.util.List;

import com.cusonar.todo.domain.Todo;

public class TodoValidator {
	
	private TodoValidator() {}
	
	public static boolean isAll(List<Todo> todos, boolean isCompleted) {
		if (todos == null) {
			return true;
		}
		
		for (Todo todo : todos) {
			if (todo.isCompleted() != isCompleted) {
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isCircularReference(Todo newTodo, List<Todo> referencedTodos) {
		List<Todo> references = newTodo.getReferences();
		
		if (references == null) {
			return false;
		}
		
		for (Todo reference : references) {
			if (newTodo.getTodoId() == reference.getTodoId()) {
				return true;
			}
		}
		
		if (referencedTodos == null) {
			return false;
		}
		
		for (Todo reference : references) {
			for (Todo referencedTodo : referencedTodos) {
				if (reference.getTodoId() == referencedTodo.getTodoId()) {
					return true;
				}
			}
		}
		
		return false;
	} 
}
