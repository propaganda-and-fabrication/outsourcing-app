package com.outsourcing.common.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.outsourcing.common.exception.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Response<T> {

	T getData();

	T getError();
	
	static <T> Response<T> of(T data, String message) {
		return new SuccessResponse<>(data, message);
	}

	static <T> Response<T> error(ErrorCode code) {
		return new ErrorResponse<>(code);
	}

	static <T> Response<T> validationError(List<ValidationErrorResponse.ValidationError> errors) {
		return new ValidationErrorResponse<>(errors);
	}
}
