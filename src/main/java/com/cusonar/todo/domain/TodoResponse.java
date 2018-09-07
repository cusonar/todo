package com.cusonar.todo.domain;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class TodoResponse {
	private long totalCount;
	private List<Todo> todos;
}
