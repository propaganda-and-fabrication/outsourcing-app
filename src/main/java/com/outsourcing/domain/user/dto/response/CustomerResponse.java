package com.outsourcing.domain.user.dto.response;

import com.outsourcing.domain.user.entity.Customer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerResponse {

	private final Long id;
	private final String name;
	private final String nickname;
	private final String email;
	private final String profileUrl;
	private final String phoneNumber;

	public static CustomerResponse of(Customer customer) {
		return new CustomerResponse(
			customer.getId(),
			customer.getName(),
			customer.getNickname(),
			customer.getEmail(),
			customer.getProfileUrl(),
			customer.getPhoneNumber()
		);
	}
}
