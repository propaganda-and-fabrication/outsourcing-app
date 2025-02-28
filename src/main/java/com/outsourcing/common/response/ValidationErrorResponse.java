package com.outsourcing.common.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse<T> implements Response<T> {

	private final List<ValidationError> errors;

	@Override
	public T getData() {
		return null;
	}

	@Override
	public T getError() {
		return null;
	}

	@Getter
	@RequiredArgsConstructor
	public static class ValidationError {    // 논리적 응집을 위해 static nested class로 정의함
		private final String code;
		private final String field;
		private final String message;
	}
}
