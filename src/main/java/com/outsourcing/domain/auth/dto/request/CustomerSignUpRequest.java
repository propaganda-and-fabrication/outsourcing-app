package com.outsourcing.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomerSignUpRequest {

	@NotBlank
	@Email
	private final String email;

	@NotBlank
	@Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
	@Pattern(regexp = ".*\\d.*", message = "비밀번호는 숫자를 포함해야 합니다.")
	@Pattern(regexp = ".*[A-Z].*", message = "비밀번호는 대문자를 포함해야 합니다.")
	private final String password;

	@NotBlank
	private final String name;

	@NotBlank
	@Pattern(regexp = "^(010)[0-9]{3,4}[0-9]{4}$", message = "휴대전화 번호 형식이 아닙니다.")
	private final String phoneNumber;

	@NotBlank
	@Pattern(regexp = "^[가-힣0-9 -]+$", message = "주소 형식이 올바르지 않습니다.")
	private final String address;

}
