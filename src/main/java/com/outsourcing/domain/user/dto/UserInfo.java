package com.outsourcing.domain.user.dto;

import java.time.LocalDateTime;

import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.enums.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInfo {

	private final Long id;
	private final String email;
	private final String password;
	private final UserRole role;
	private final LocalDateTime deletedAt;

	public static UserInfo of(User user) {
		return new UserInfo(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getDeletedAt());
	}

}
