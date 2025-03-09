package com.outsourcing.domain.user.dto.request.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AddAddressRequest {

	@NotBlank
	@Pattern(regexp = "^[가-힣0-9 -]+$", message = "주소 형식이 올바르지 않습니다.")
	private final String address;
}
