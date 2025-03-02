package com.outsourcing.domain.user.dto.request.customer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddAddressRequest {

	@NotBlank
	private final String address;
}
