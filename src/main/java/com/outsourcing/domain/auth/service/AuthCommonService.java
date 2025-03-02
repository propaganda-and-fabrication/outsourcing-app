package com.outsourcing.domain.auth.service;

import static com.outsourcing.common.exception.ErrorCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.repository.RefreshTokenRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthCommonService {

	private final JwtTokenProvider tokenProvider;
	private final RefreshTokenRepositoryImpl refreshTokenRepository;

	@Transactional
	public void logout(String accessToken, String refreshToken, CustomUserDetails currentUser) {
		String accessTokenWithoutBearer = tokenProvider.substringToken(accessToken);

		if (!tokenProvider.isTokenValidated(accessTokenWithoutBearer)) {
			throw new BaseException(INVALID_TOKEN);
		}

		String email = currentUser.getUsername();
		// redis에 로그인 한 유저의 refreshToken이 null이 아니고, refreshToken의 email이 로그인한 유저의 이메일과 동일하면 삭제
		if (refreshTokenRepository.getKey(email) != null && tokenProvider.getSubject(refreshToken).equals(email)) {
			refreshTokenRepository.delete(email);
		}

		long expiration = tokenProvider.extractClaims(accessTokenWithoutBearer).getExpiration().getTime();

		refreshTokenRepository.addBlacklist(accessToken, expiration);
	}
}
