package com.outsourcing.common.response;

import java.util.List;

import com.outsourcing.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SuccessResponse<T> implements Response<T> {

	private final T data;
	private final String message;

	@Override
	public ErrorCode getError() {
		return null;
	}

	@Override
	public List<ValidationErrorResponse.ValidationError> getErrors() {
		return null;
	}

}
