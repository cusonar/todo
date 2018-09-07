package com.cusonar.todo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cusonar.todo.domain.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {
	public List<Todo> findByReferencesIn(List<Todo> references);
	@Query(value = "SELECT MAX(last_modified_date) FROM todo", nativeQuery = true)
	public LocalDateTime getMaxLastModifiedDate();
}
