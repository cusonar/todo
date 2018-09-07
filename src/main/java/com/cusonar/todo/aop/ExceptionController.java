package com.cusonar.todo.aop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.cusonar.todo.domain.ErrorResponse;
import com.cusonar.todo.exception.TodoException;
import com.cusonar.todo.exception.TodoNotFoundException;
import com.cusonar.todo.exception.TodoReferenceException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionController {
	
	@ExceptionHandler(TodoNotFoundException.class)
	public ResponseEntity<ErrorResponse> todoNotFoundExceptionHandler(Exception e) {
		return commonHandler(e, e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = {TodoException.class, TodoReferenceException.class})
	public ResponseEntity<ErrorResponse> todoExceptionHandler(Exception e) {
		return commonHandler(e, e.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchExceptionHandler(Exception e) {
		return commonHandler(e, "The parameters is wrong.", HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
		return commonHandler(e, "Unknown Error", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private ResponseEntity<ErrorResponse> commonHandler(Exception e, String message, HttpStatus status) {
		log.error(e.getMessage(), e);
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(status.value());
		error.setMessage(message);
		return new ResponseEntity<>(error, status);
	}
}
