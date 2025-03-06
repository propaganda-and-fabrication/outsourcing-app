package com.outsourcing.domain.auth.service;

import static com.outsourcing.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthCommonService {

	private final JwtTokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public void logout(String accessToken, String refreshToken, CustomUserDetails currentUser) {
		String accessTokenWithoutBearer = tokenProvider.substringToken(accessToken);

		if (!tokenProvider.isTokenValidated(accessTokenWithoutBearer)) {
			throw new BaseException(INVALID_TOKEN);
		}

		String email = currentUser.getUsername();
		// redis에 저장된 refreshToken과 요청 본문으로 받은 refreshToken의 값이 다르거나 
		// refreshToken의 subject와 로그인한 유저의 이메일이 다를 경우 예외 발생
		if (!refreshTokenRepository.getValueByKey(email).equals(refreshToken)
			|| !tokenProvider.getSubject(refreshToken).equals(email)) {
			throw new BaseException(INVALID_TOKEN);
		}
		refreshTokenRepository.delete(email);

		long expiration = tokenProvider.extractClaims(accessTokenWithoutBearer).getExpiration().getTime();

		refreshTokenRepository.addBlacklist(accessToken, expiration);
	}
}
