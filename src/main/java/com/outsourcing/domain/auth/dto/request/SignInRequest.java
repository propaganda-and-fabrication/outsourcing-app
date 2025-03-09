package com.outsourcing.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignInRequest {

	@NotBlank
	private final String email;

	@NotBlank
	private final String password;
}
