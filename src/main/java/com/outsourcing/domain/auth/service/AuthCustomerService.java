package com.outsourcing.domain.auth.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.AddressStatus.*;
import static com.outsourcing.domain.user.enums.UserRole.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.dto.response.TokenResponse;
import com.outsourcing.domain.auth.entity.RefreshToken;
import com.outsourcing.domain.auth.repository.RefreshTokenRepository;
import com.outsourcing.domain.user.entity.Address;
import com.outsourcing.domain.user.entity.Customer;
import com.outsourcing.domain.user.repository.AddressRepository;
import com.outsourcing.domain.user.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthCustomerService {

	private final CustomerRepository customerRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider tokenProvider;
	private final AddressRepository addressRepository;

	@Transactional
	public TokenResponse signUpCustomer(
		String email,
		String password,
		String name,
		String phoneNumber,
		String address
	) {
		// 이메일 중복 검사
		if (customerRepository.existsByEmail(email)) {
			throw new BaseException(EMAIL_DUPLICATED);
		}

		// 전화번호 중복 검사
		if (customerRepository.existsByPhoneNumber(phoneNumber)) {
			throw new BaseException(PHONE_NUMBER_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(password);
		Customer newCustomer = customerRepository.save(
			new Customer(email, encodedPassword, name, phoneNumber, CUSTOMER));

		addressRepository.save(Address.from(address, ACTIVE, newCustomer));

		String accessToken = tokenProvider.generateAccessToken(newCustomer.getId(), newCustomer.getEmail(),
			newCustomer.getRole());
		String refreshToken = tokenProvider.generateRefreshToken(newCustomer.getEmail());

		// 생성된 refreshToken을 redis에 저장
		refreshTokenRepository.save(new RefreshToken(newCustomer.getEmail(), refreshToken));

		return TokenResponse.of(newCustomer.getId(), accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse signInCustomer(String email, String password) {
		Customer getCustomer = customerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(LOGIN_FAILED));

		checkPassword(password, getCustomer.getPassword());

		String accessToken = tokenProvider.generateAccessToken(getCustomer.getId(),
			getCustomer.getEmail(), getCustomer.getRole());
		String refreshToken = tokenProvider.generateRefreshToken(getCustomer.getEmail());

		refreshTokenRepository.save(new RefreshToken(getCustomer.getEmail(), refreshToken));

		return TokenResponse.of(getCustomer.getId(), accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse customerTokenReissue(String refreshToken) {
		if (!tokenProvider.isTokenValidated(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		String email = tokenProvider.getSubject(refreshToken);

		RefreshToken getRefreshToken = refreshTokenRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(INVALID_TOKEN));

		if (!getRefreshToken.getRefreshToken().equals(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		Customer getCustomer = getActiveCustomerByEmail(email);

		String newRefreshToken = tokenProvider.generateRefreshToken(getCustomer.getEmail());
		String newAccessToken = tokenProvider.generateAccessToken(getCustomer.getId(), getCustomer.getEmail(),
			getCustomer.getRole());

		getRefreshToken.updateRefreshToken(newRefreshToken);
		refreshTokenRepository.save(getRefreshToken);

		return TokenResponse.of(getCustomer.getId(), newAccessToken, newRefreshToken);
	}

	private Customer getActiveCustomerByEmail(String email) {
		return customerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	private void checkPassword(String password, String storedPassword) {
		if (!passwordEncoder.matches(password, storedPassword)) {
			throw new BaseException(LOGIN_FAILED);
		}
	}
}
