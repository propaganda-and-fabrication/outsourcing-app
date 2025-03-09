package com.outsourcing.common.exception;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationErrorDto {

	private final List<ValidationError> errors;

	@Getter
	@RequiredArgsConstructor
	public static class ValidationError {    // 논리적 응집을 위해 static nested class로 정의함
		private final String code;
		private final String field;
		private final String message;
	}
}
