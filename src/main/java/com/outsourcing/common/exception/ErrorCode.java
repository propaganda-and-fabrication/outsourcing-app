package com.outsourcing.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 인증 관련 예외
	INVALID_ROLE(BAD_REQUEST, "유효하지 않은 역할입니다."),
	INVALID_TOKEN_SIGNATURE(BAD_REQUEST, "유효하지 않은 토큰 서명입니다."),
	INVALID_TOKEN(BAD_REQUEST, "유효하지 않은 토큰입니다."),
	TOKEN_ALREADY_EXPIRED(BAD_REQUEST, "만료된 토큰입니다."),
	UNSUPPORTED_TOKEN(BAD_REQUEST, "지원되지 않는 토큰입니다."),
	MISSING_AUTHORIZATION_HEADER(UNAUTHORIZED, "JWT 토큰이 존재하지 않습니다."),
	MISSING_AUTHENTICATION_INFORMATION(UNAUTHORIZED, "인증 정보가 누락되었습니다."),
	ACCESS_DENIED(FORBIDDEN, "권한이 부족합니다."),
	LOGIN_FAILED(BAD_REQUEST, "로그인에 실패했습니다."),
	EMAIL_DUPLICATED(CONFLICT, "중복된 이메일입니다."),
	PHONE_NUMBER_DUPLICATED(CONFLICT, "중복된 휴대폰 번호입니다."),

	// 유저 관련 예외
	USER_NOT_FOUND(NOT_FOUND, "유저를 찾을 수 없습니다."),

	// 주문 관련 예외

	// 이후 내용을 추가

	TYPE_MISMATCH(BAD_REQUEST, "잘못된 타입입니다."),

	// 서버 관련 예외
	SERVER_NOT_WORK(INTERNAL_SERVER_ERROR, "서버 문제로 인해 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
