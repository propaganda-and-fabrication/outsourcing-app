package com.outsourcing.domain.auth.dto.request;

import static com.outsourcing.domain.user.enums.UserRole.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OwnerSignUpRequest {

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

	private final String userRole;

	@Builder
	public OwnerSignUpRequest(String email, String password, String name, String phoneNumber) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.userRole = OWNER.getAuthority();
	}
}
