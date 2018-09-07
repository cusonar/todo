package com.cusonar.todo.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@ToString
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "todo")
@EntityListeners(AuditingEntityListener.class)
public class Todo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long todoId;
	private String title;
	@Column(updatable = false, nullable = false)
	@CreatedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;
	@Column(nullable = false)
	@LastModifiedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastModifiedDate;
	private boolean completed;
	@ManyToMany(cascade = {}, fetch = FetchType.LAZY)
	private List<Todo> references;
	
	public Todo(Long todoId) {
		this.todoId = todoId;
		this.title = "";
		this.completed = false;
	}
	public Todo(String title, boolean completed, Todo... references) {
		this.title = title;
		this.completed = completed;
		if (references.length > 0) {
			this.references = new ArrayList<>(Arrays.asList(references));
		}
	}
	public Todo(Long todoId, String title, boolean completed, Todo... references) {
		this(title, completed, references);
		this.todoId = todoId;
	}
}
