package com.outsourcing.domain.auth.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.UserRole.*;

import java.time.Duration;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.dto.response.TokenResponse;
import com.outsourcing.domain.auth.entity.RefreshToken;
import com.outsourcing.domain.auth.repository.RefreshTokenRepositoryImpl;
import com.outsourcing.domain.user.entity.Customer;
import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;
	private final RefreshTokenRepositoryImpl refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public TokenResponse signUp(String email, String password, String name, String phoneNumber, String role) {
		// 이메일 중복 검사
		if (userRepository.existsByEmail(email)) {
			throw new BaseException(EMAIL_DUPLICATED);
		}

		// 전화번호 중복 검사
		if (userRepository.existsByPhoneNumber(phoneNumber)) {
			throw new BaseException(PHONE_NUMBER_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(password);
		Customer newCustomer = userRepository.save(
			new Customer(email, encodedPassword, name, phoneNumber, from(role)));

		String accessToken = tokenProvider.generateAccessToken(newCustomer.getId(), newCustomer.getEmail(),
			newCustomer.getRole());
		String refreshToken = tokenProvider.generateRefreshToken(newCustomer.getEmail());

		// 생성된 refreshToken을 redis에 저장
		refreshTokenRepository.save(new RefreshToken(newCustomer.getEmail(), refreshToken));

		return TokenResponse.of(newCustomer.getId(), accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse signIn(String email, String password) {
		User getUser = userRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(LOGIN_FAILED));

		if (!passwordEncoder.matches(password, getUser.getPassword())) {
			throw new BaseException(LOGIN_FAILED);
		}

		String accessToken = tokenProvider.generateAccessToken(getUser.getId(), getUser.getEmail(),
			getUser.getRole());
		String refreshToken = tokenProvider.generateRefreshToken(getUser.getEmail());

		refreshTokenRepository.save(new RefreshToken(getUser.getEmail(), refreshToken));

		return TokenResponse.of(getUser.getId(), accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse tokenReissue(String refreshToken) {
		if (!jwtTokenProvider.isTokenValidated(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		String email = jwtTokenProvider.getSubject(refreshToken);

		RefreshToken getRefreshToken = refreshTokenRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(INVALID_TOKEN));

		if (!getRefreshToken.getRefreshToken().equals(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		User getUser = userRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));

		String newRefreshToken = jwtTokenProvider.generateRefreshToken(getUser.getEmail());
		String newAccessToken = jwtTokenProvider.generateAccessToken(getUser.getId(), getUser.getEmail(),
			getUser.getRole());

		getRefreshToken.updateRefreshToken(newRefreshToken);
		refreshTokenRepository.save(getRefreshToken);

		return TokenResponse.of(getUser.getId(), newAccessToken, newRefreshToken);
	}

	public void logout(String accessToken, String refreshToken, CustomUserDetails userDetails) {
		String accessTokenWithoutBearer = tokenProvider.substringToken(accessToken);

		if (!tokenProvider.isTokenValidated(accessTokenWithoutBearer)) {
			throw new BaseException(INVALID_TOKEN);
		}

		String email = userDetails.getUsername();
		// redis에 로그인 한 유저의 refreshToken이 null이 아니고, refreshToken의 email이 로그인한 유저의 이메일과 동일하면 삭제
		if (refreshTokenRepository.getKey(email) != null && tokenProvider.getSubject(refreshToken).equals(email)) {
			refreshTokenRepository.delete(email);
		}

		long expiration = tokenProvider.extractClaims(accessTokenWithoutBearer).getExpiration().getTime();
		long currentTime = System.currentTimeMillis();

		if (expiration - currentTime > 0) {
			refreshTokenRepository.addBlacklist(accessToken, Duration.ofMillis(expiration - currentTime));
		} else {
			throw new BaseException(TOKEN_ALREADY_EXPIRED);
		}
	}
}
