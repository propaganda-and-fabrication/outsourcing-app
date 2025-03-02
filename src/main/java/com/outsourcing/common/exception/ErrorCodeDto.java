package com.outsourcing.common.exception;

import lombok.Getter;

@Getter
public class ErrorCodeDto {

	private final String codeName;
	private final String message;

	public ErrorCodeDto(ErrorCode errorCode) {
		this.codeName = errorCode.name();
		this.message = errorCode.getMessage();
	}
}
