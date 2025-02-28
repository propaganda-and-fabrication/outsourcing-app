package com.outsourcing.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SuccessResponse<T> implements Response<T> {

	private final T data;
	private final String message;

	@Override
	public T getError() {
		return null;
	}
}
