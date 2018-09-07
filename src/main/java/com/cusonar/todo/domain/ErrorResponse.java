package com.cusonar.todo.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class ErrorResponse {
	private int errorCode;
	private String message;
}
