package com.outsourcing.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LogoutRequest {

	@NotBlank
	private final String accessToken;

	@NotBlank
	private final String refreshToken;
}
