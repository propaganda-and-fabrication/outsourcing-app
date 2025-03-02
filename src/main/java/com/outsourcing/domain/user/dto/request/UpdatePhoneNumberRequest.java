package com.outsourcing.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdatePhoneNumberRequest {

	@NotBlank
	@Pattern(regexp = "^(010)[0-9]{3,4}[0-9]{4}$", message = "휴대전화 번호 형식이 아닙니다.")
	private final String newPhoneNumber;
}
