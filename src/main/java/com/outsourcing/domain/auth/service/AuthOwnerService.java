package com.outsourcing.domain.auth.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.UserRole.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.dto.response.TokenResponse;
import com.outsourcing.domain.auth.entity.RefreshToken;
import com.outsourcing.domain.auth.repository.RefreshTokenRepository;
import com.outsourcing.domain.user.entity.Owner;
import com.outsourcing.domain.user.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthOwnerService {

	private final OwnerRepository ownerRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider tokenProvider;

	@Transactional
	public TokenResponse signUpOwner(String email, String password, String name, String phoneNumber, String role) {
		// 이메일 중복 검사
		if (ownerRepository.existsByEmail(email)) {
			throw new BaseException(EMAIL_DUPLICATED);
		}

		// 전화번호 중복 검사
		if (ownerRepository.existsByPhoneNumber(phoneNumber)) {
			throw new BaseException(PHONE_NUMBER_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(password);
		Owner newOwner = ownerRepository.save(
			new Owner(email, encodedPassword, name, phoneNumber, from(role)));

		String accessToken = tokenProvider.generateAccessToken(newOwner.getId(), newOwner.getEmail(),
			newOwner.getRole());
		String refreshToken = tokenProvider.generateRefreshToken(newOwner.getEmail());

		// 생성된 refreshToken을 redis에 저장
		refreshTokenRepository.save(new RefreshToken(newOwner.getEmail(), refreshToken));

		return TokenResponse.of(newOwner.getId(), accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse signInOwner(String email, String password) {
		Owner getOwner = ownerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(LOGIN_FAILED));

		checkPassword(password, getOwner.getPassword());

		String accessToken = tokenProvider.generateAccessToken(getOwner.getId(), getOwner.getEmail(),
			getOwner.getRole());
		String refreshToken = tokenProvider.generateRefreshToken(getOwner.getEmail());

		refreshTokenRepository.save(new RefreshToken(getOwner.getEmail(), refreshToken));

		return TokenResponse.of(getOwner.getId(), accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse ownerTokenReissue(String refreshToken) {
		if (!tokenProvider.isTokenValidated(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		String email = tokenProvider.getSubject(refreshToken);

		RefreshToken getRefreshToken = refreshTokenRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(INVALID_TOKEN));

		if (!getRefreshToken.getRefreshToken().equals(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		Owner getOwner = getActiveOwnerByEmail(email);

		String newRefreshToken = tokenProvider.generateRefreshToken(getOwner.getEmail());
		String newAccessToken = tokenProvider.generateAccessToken(getOwner.getId(), getOwner.getEmail(),
			getOwner.getRole());

		getRefreshToken.updateRefreshToken(newRefreshToken);
		refreshTokenRepository.save(getRefreshToken);

		return TokenResponse.of(getOwner.getId(), newAccessToken, newRefreshToken);
	}

	private Owner getActiveOwnerByEmail(String email) {
		return ownerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	private void checkPassword(String password, String storedPassword) {
		if (!passwordEncoder.matches(password, storedPassword)) {
			throw new BaseException(LOGIN_FAILED);
		}
	}
}
