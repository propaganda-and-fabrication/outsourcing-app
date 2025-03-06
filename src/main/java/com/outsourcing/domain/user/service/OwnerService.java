package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.repository.RefreshTokenRepository;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.dto.response.OwnerResponse;
import com.outsourcing.domain.user.entity.Owner;
import com.outsourcing.domain.user.repository.OwnerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

	private final OwnerRepository ownerRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider tokenProvider;

	@Transactional(readOnly = true)
	public OwnerResponse getOwnerProfile(CustomUserDetails currentUser) {
		Owner getOwner = getActiveOwnerByEmail(currentUser.getUsername());
		return OwnerResponse.of(getOwner);
	}

	@Transactional
	public OwnerResponse updatePhoneNumber(String newPhoneNumber, CustomUserDetails currentUser) {
		Owner getOwner = getOwnerOrElseThrow(currentUser.getUsername());

		// 휴대폰 번호가 이미 존재함
		if (ownerRepository.existsByPhoneNumber(newPhoneNumber)) {
			throw new BaseException(PHONE_NUMBER_DUPLICATED);
		}

		// 휴대폰 번호가 이미 존재함
		if (getOwner.getPhoneNumber().equals(newPhoneNumber)) {
			throw new BaseException(PHONE_NUMBER_SAME_AS_OLD);
		}

		getOwner.changePhoneNumber(newPhoneNumber);
		return OwnerResponse.of(getOwner);
	}

	@Transactional
	public OwnerResponse updatePassword(String oldPassword, String newPassword, CustomUserDetails currentUser) {
		Owner getOwner = getActiveOwnerByEmail(currentUser.getUsername());

		// 비밀번호 불일치
		if (!passwordEncoder.matches(oldPassword, getOwner.getPassword())) {
			throw new BaseException(PASSWORD_NOT_MATCHED);
		}

		// 이전 비밀번호와 바꾸려는 비밀번호가 동일
		if (passwordEncoder.matches(newPassword, getOwner.getPassword())) {
			throw new BaseException(PASSWORD_SAME_AS_OLD);
		}

		getOwner.changePassword(passwordEncoder.encode(newPassword));
		return OwnerResponse.of(getOwner);
	}

	@Transactional
	public void deleteOwner(
		String password,
		String accessToken,
		String refreshToken,
		CustomUserDetails currentUser
	) {
		if (!passwordEncoder.matches(password, currentUser.getPassword())) {
			throw new BaseException(PASSWORD_NOT_MATCHED);
		}

		String email = currentUser.getUsername();

		// redis에 저장된 refreshToken과 요청 본문으로 받은 refreshToken의 값이 다르거나
		// refreshToken의 subject와 로그인한 유저의 이메일이 다를 경우 예외 발생
		if (!refreshTokenRepository.getValueByKey(email).equals(refreshToken)
			|| !tokenProvider.getSubject(refreshToken).equals(email)) {
			throw new BaseException(INVALID_TOKEN);
		}
		refreshTokenRepository.delete(email);
		ownerRepository.softDeleteOwner();

		String accessTokenWithoutBearer = tokenProvider.substringToken(accessToken);
		long expiration = tokenProvider.extractClaims(accessTokenWithoutBearer).getExpiration().getTime();

		refreshTokenRepository.addBlacklist(accessToken, expiration);
	}

	// 탈퇴한 사용자까지 조회
	private Owner getOwnerOrElseThrow(String email) {
		return ownerRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	// 탈퇴한 사용자 제외 조회
	private Owner getActiveOwnerByEmail(String email) {
		return ownerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}
}
