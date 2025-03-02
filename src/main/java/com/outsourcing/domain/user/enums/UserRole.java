package com.outsourcing.domain.user.enums;

import static com.outsourcing.common.exception.ErrorCode.*;

import com.outsourcing.common.exception.BaseException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

	CUSTOMER("ROLE_CUSTOMER"),
	OWNER("ROLE_OWNER");

	private final String authority;

	public static UserRole from(String role) {
		for (UserRole value : values()) {
			if (value.authority.equalsIgnoreCase(role)) {
				return value;
			}
		}
		throw new BaseException(INVALID_ROLE);
	}
}
