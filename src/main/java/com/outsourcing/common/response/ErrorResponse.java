package com.outsourcing.common.response;

import com.outsourcing.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse<T> implements Response<T> {

	private final ErrorCode error;

	@Override
	public T getData() {
		return null;
	}
}
