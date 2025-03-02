package com.outsourcing.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorCodeDto {

	private final ErrorCode error;
	private final String message;

}
