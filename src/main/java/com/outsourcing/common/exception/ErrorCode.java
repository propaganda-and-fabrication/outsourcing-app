package com.outsourcing.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 인증 관련 얘외

	// 유저 관련 예외

	// 주문 관련 얘외

	// 이후 내용을 추가

	TYPE_MISMATCH(BAD_REQUEST, "잘못된 타입입니다."),

	// 서버 관련 얘외
	SERVER_NOT_WORK(INTERNAL_SERVER_ERROR, "서버 문제로 인해 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
