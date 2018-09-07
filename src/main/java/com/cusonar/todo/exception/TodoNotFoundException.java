package com.cusonar.todo.exception;

public class TodoNotFoundException extends TodoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1692695299383572726L;
	public TodoNotFoundException(String message) {
		super(message);
	}
}
