package com.cusonar.todo;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.cusonar.todo.domain.Todo;
import com.cusonar.todo.repository.TodoRepository;

@SpringBootApplication
public class TodoApplication implements CommandLineRunner {

	@Autowired private TodoRepository todoRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Todo todo1 = new Todo("집안일", false);
		Todo todo2 = new Todo("빨래", false, todo1);
		Todo todo3 = new Todo("청소", false, todo1);
		Todo todo4 = new Todo("방청소", false, todo1, todo3);
		Todo todo5 = new Todo("이불빨래", true, todo2);
		Todo todo6 = new Todo("이불커버빨래", true, todo2, todo5);
		Todo todo7 = new Todo("거실청소", false);
		Todo todo8 = new Todo("화장실청소", true);
		todoRepository.saveAll(Arrays.asList(todo1, todo2, todo3, todo4, todo5, todo6, todo7, todo8));
	}
}
