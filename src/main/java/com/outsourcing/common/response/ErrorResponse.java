package com.outsourcing.common.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(NON_NULL)
public class ErrorResponse<T> implements Response<T> {

	private final T error;
	private String message;

	public ErrorResponse(T error) {
		this.error = error;
	}

	public ErrorResponse(T error, String message) {
		this.error = error;
		this.message = message;
	}

	@Override
	public T getData() {
		return null;
	}

}
