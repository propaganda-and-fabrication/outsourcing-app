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
import com.outsourcing.domain.auth.repository.RefreshTokenRepositoryImpl;
import com.outsourcing.domain.user.entity.Address;
import com.outsourcing.domain.user.entity.Customer;
import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.AddressRepository;
import com.outsourcing.domain.user.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final CustomerRepository customerRepository;
	private final AddressRepository addressRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider tokenProvider;
	private final RefreshTokenRepositoryImpl refreshTokenRepository;

	@Transactional
	public TokenResponse signUpCustomer(String email, String password, String name, String phoneNumber, String role,
		String address) {
		// 이메일 중복 검사
		if (customerRepository.existsByEmail(email)) {
			throw new BaseException(EMAIL_DUPLICATED);
		}

		// 전화번호 중복 검사
		if (customerRepository.existsByPhoneNumber(phoneNumber)) {
			throw new BaseException(PHONE_NUMBER_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(password);
		Customer newCustomer = new Customer(email, encodedPassword, name, phoneNumber, from(role));

		Address newAddress = Address.from(address);
		newAddress.updateStatus(ACTIVE);
		newCustomer.addAddress(newAddress);
		customerRepository.save(newCustomer);

		String accessToken = tokenProvider.generateAccessToken(newCustomer.getId(), newCustomer.getEmail(),
			newCustomer.getRole());
		String refreshToken = tokenProvider.generateRefreshToken(newCustomer.getEmail());

		// 생성된 refreshToken을 redis에 저장
		refreshTokenRepository.save(new RefreshToken(newCustomer.getEmail(), refreshToken));

		return TokenResponse.of(newCustomer.getId(), accessToken, refreshToken);
	}

	@Transactional
	public TokenResponse signUpOwner(String email, String password, String name, String phoneNumber, String role) {
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
		User getUser = customerRepository.findByEmailAndDeletedAt(email)
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
		if (!tokenProvider.isTokenValidated(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		String email = tokenProvider.getSubject(refreshToken);

		RefreshToken getRefreshToken = refreshTokenRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(INVALID_TOKEN));

		if (!getRefreshToken.getRefreshToken().equals(refreshToken)) {
			throw new BaseException(INVALID_TOKEN);
		}

		User getUser = getUserOrElseThrow(email);

		String newRefreshToken = tokenProvider.generateRefreshToken(getUser.getEmail());
		String newAccessToken = tokenProvider.generateAccessToken(getUser.getId(), getUser.getEmail(),
			getUser.getRole());

		getRefreshToken.updateRefreshToken(newRefreshToken);
		refreshTokenRepository.save(getRefreshToken);

		return TokenResponse.of(getUser.getId(), newAccessToken, newRefreshToken);
	}

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

	private User getUserOrElseThrow(String email) {
		return customerRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}
}
