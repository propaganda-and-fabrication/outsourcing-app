package com.outsourcing.domain.user.dto.response;

import com.outsourcing.domain.user.entity.Customer;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerResponse {

	private final String name;
	private final String nickname;
	private final String email;
	private final String profileUrl;
	private final String phoneNumber;

	public static CustomerResponse of(Customer customer) {
		return new CustomerResponse(
			customer.getName(),
			customer.getNickname(),
			customer.getEmail(),
			customer.getProfileUrl(),
			customer.getPhoneNumber()
		);
	}
}
