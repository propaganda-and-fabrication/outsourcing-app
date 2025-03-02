package com.outsourcing.domain.user.dto.request.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateNicknameRequest {

	@NotBlank
	@Size(min = 2, max = 12, message = "닉네임은 2 ~ 12 글자 사이여야 합니다.")
	private final String changeNickname;
}
