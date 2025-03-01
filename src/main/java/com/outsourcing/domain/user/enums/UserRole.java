package com.outsourcing.domain.user.enums;

import static com.outsourcing.common.exception.ErrorCode.*;

import com.outsourcing.common.exception.BaseException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {

	ROLE_CUSTOMER, ROLE_OWNER;

	public static UserRole from(String role) {
		for (UserRole value : values()) {
			if (value.toString().equalsIgnoreCase(role)) {
				return value;
			}
		}
		throw new BaseException(INVALID_ROLE);
	}
}
