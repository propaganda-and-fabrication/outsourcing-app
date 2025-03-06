package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.entity.RefreshToken;
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

		if (getOwner.getPhoneNumber().equals(newPhoneNumber)) {
			throw new BaseException(PHONE_NUMBER_SAME_AS_OLD);
		}

		getOwner.changePhoneNumber(newPhoneNumber);
		return OwnerResponse.of(getOwner);
	}

	@Transactional
	public OwnerResponse updatePassword(String oldPassword, String newPassword, CustomUserDetails currentUser) {
		Owner getOwner = getActiveOwnerByEmail(currentUser.getUsername());

		if (!passwordEncoder.matches(oldPassword, getOwner.getPassword())) {
			throw new BaseException(PASSWORD_NOT_MATCHED);
		}

		if (passwordEncoder.matches(newPassword, getOwner.getPassword())) {
			throw new BaseException(PASSWORD_SAME_AS_OLD);
		}

		getOwner.changePassword(passwordEncoder.encode(newPassword));
		return OwnerResponse.of(getOwner);
	}

	@Transactional
	public void deleteOwner(String password, String accessToken, CustomUserDetails currentUser) {
		if (!passwordEncoder.matches(password, currentUser.getPassword())) {
			throw new BaseException(PASSWORD_NOT_MATCHED);
		}

		ownerRepository.softDeleteOwner();

		String email = currentUser.getUsername();
		RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(INVALID_TOKEN));

		if (refreshTokenRepository.getValueByKey(email) != null
			&& tokenProvider.getSubject(refreshToken.getRefreshToken()).equals(email)) {
			refreshTokenRepository.delete(email);
		}

		String accessTokenWithoutBearer = tokenProvider.substringToken(accessToken);
		long expiration = tokenProvider.extractClaims(accessTokenWithoutBearer).getExpiration().getTime();

		refreshTokenRepository.addBlacklist(accessToken, expiration);
	}

	private Owner getOwnerOrElseThrow(String email) {
		return ownerRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	private Owner getActiveOwnerByEmail(String email) {
		return ownerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}
}
