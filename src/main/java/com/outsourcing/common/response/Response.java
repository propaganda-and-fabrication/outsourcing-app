package com.outsourcing.common.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.outsourcing.common.exception.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface Response<T> {

	T getData();

	ErrorCode getError();

	List<ValidationErrorResponse.ValidationError> getErrors();

	static <T> Response<T> of(T data, String message) {
		return new SuccessResponse<>(data, message);
	}

	static <T> Response<T> error(ErrorCode code, String message) {
		return new ErrorResponse<>(code, message);
	}

	static <T> Response<T> validationError(List<ValidationErrorResponse.ValidationError> errors) {
		return new ValidationErrorResponse<>(errors);
	}
}
