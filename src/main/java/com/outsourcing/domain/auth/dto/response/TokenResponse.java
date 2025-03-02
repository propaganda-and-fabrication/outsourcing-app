package com.outsourcing.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TokenResponse {

	private final Long id;
	private final String accessToken;
	private final String refreshToken;

	public static TokenResponse of(Long id, String accessToken, String refreshToken) {
		return new TokenResponse(id, accessToken, refreshToken);
	}
}
