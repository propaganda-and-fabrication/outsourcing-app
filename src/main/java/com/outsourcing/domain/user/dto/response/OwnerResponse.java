package com.outsourcing.domain.user.dto.response;

import com.outsourcing.domain.user.entity.Owner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OwnerResponse {

	private final Long id;
	private final String name;
	private final String phoneNumber;
	private final String profileUrl;
	private final String constantNickname;
	private final int storeCount;

	public static OwnerResponse of(Owner owner) {
		return new OwnerResponse(
			owner.getId(),
			owner.getName(),
			owner.getPhoneNumber(),
			owner.getProfileUrl(),
			owner.getConstantNickname(),
			owner.getStoreCount()
		);
	}
}
