package com.outsourcing.common.storage.enums;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.UserRole.*;

import java.util.function.Predicate;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.domain.user.enums.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UploadType {

	PROFILES("profiles", role -> role == CUSTOMER),
	MENUS("menus", role -> role == OWNER),
	REVIEWS("reviews", role -> true),
	STORES("stores", role -> role == OWNER);

	private final String type;

	// if 분기를 제거할 수 있고, 폴더에 업로드 할 수 있는 역할을 폴더 타입에 따라 바로 나눌 수 있음
	private final Predicate<UserRole> roleChecker;

	public static UploadType from(String type) {
		for (UploadType uploadType : values()) {
			if (uploadType.type.equalsIgnoreCase(type)) {
				return uploadType;
			}
		}
		throw new BaseException(TYPE_MISMATCH);
	}

	public String getPrefix() {
		return this.type + "/";
	}

	public boolean isAllowedFor(UserRole role) {
		return roleChecker.test(role);    // Predicate 사용 메서드
	}
}
