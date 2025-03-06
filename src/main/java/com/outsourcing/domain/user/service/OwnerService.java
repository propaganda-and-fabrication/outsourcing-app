package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
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

		// 휴대폰 번호 이전과 동일함
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
