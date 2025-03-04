package com.outsourcing.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;
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

	// 유저 관련 예외
	USER_NOT_FOUND(NOT_FOUND, "유저를 찾을 수 없습니다."),
	NICKNAME_DUPLICATED(CONFLICT, "중복된 닉네임입니다."),
	EMAIL_DUPLICATED(CONFLICT, "중복된 이메일입니다."),
	PHONE_NUMBER_DUPLICATED(CONFLICT, "중복된 휴대폰 번호입니다."),
	NICKNAME_SAME_AS_OLD(BAD_REQUEST, "이전 닉네임과 동일할 수 없습니다."),
	PHONE_NUMBER_SAME_AS_OLD(BAD_REQUEST, "이전 휴대폰 번호와 동일할 수 없습니다."),
	PASSWORD_SAME_AS_OLD(BAD_REQUEST, "이전 비밀번호와 동일할 수 없습니다."),
	PASSWORD_NOT_MATCHED(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

	// 주소 관련 예외
	ADDRESS_NOT_FOUND(NOT_FOUND, "주소를 찾을 수 없습니다."),
	INVALID_ADDRESS_ACTIVE(BAD_REQUEST, "주소 선택은 한 군데만 할 수 있습니다."),
	ADDRESS_STATUS_IS_ALREADY_ACTIVE(BAD_REQUEST, "이미 선택된 주소입니다."),
	NO_ACTIVE_ADDRESS(BAD_REQUEST, "최소 한 군데는 주소로 선택해야 합니다."),
	ADDRESS_SAME_AS_OLD(BAD_REQUEST, "이전 주소와 동일할 수 없습니다."),
	DELETE_ADDRESS_FAILED(BAD_REQUEST, "선택되어 있는 주소는 삭제할 수 없습니다."),
	ADDRESS_ACCESS_DENIED(FORBIDDEN, "해당 주소에 대한 권한이 없습니다."),

	// 가게 관련 예외
	MAX_STORE_LIMIT_REACHED(BAD_REQUEST, "운영하는 가게 수가 3개를 초과하였습니다."),

	// 주문 관련 예외

	// 이후 내용을 추가

	TYPE_MISMATCH(BAD_REQUEST, "잘못된 타입입니다."),

	// 서버 관련 예외
	SERVER_NOT_WORK(INTERNAL_SERVER_ERROR, "서버 문제로 인해 실패했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	public void apply(HttpServletResponse response) {
		response.setStatus(this.getHttpStatus().value());
	}
}
