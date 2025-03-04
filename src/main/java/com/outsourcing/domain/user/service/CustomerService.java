package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.AddressStatus.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.entity.RefreshToken;
import com.outsourcing.domain.auth.repository.RefreshTokenRepository;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.dto.response.CustomerResponse;
import com.outsourcing.domain.user.dto.response.GetAllAddressResponse;
import com.outsourcing.domain.user.entity.Address;
import com.outsourcing.domain.user.entity.Customer;
import com.outsourcing.domain.user.repository.AddressRepository;
import com.outsourcing.domain.user.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final AddressRepository addressRepository;
	private final PasswordEncoder passwordEncoder;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider tokenProvider;

	@Transactional(readOnly = true)
	public CustomerResponse getCustomerProfile(CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());
		return CustomerResponse.of(getCustomer);
	}

	@Transactional
	public CustomerResponse updateNickname(String changeNickname, CustomUserDetails currentUser) {
		if (customerRepository.existsByNickname(changeNickname)) {
			throw new BaseException(NICKNAME_DUPLICATED);
		}

		Customer getCustomer = getCustomerOrElseThrow(currentUser.getUsername());
		if (getCustomer.getNickname().equals(changeNickname)) {
			throw new BaseException(NICKNAME_SAME_AS_OLD);
		}

		getCustomer.changeNickname(changeNickname);

		return CustomerResponse.of(getCustomer);
	}

	@Transactional
	public GetAllAddressResponse addAddress(String address, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());

		String strippedAddress = address.strip();
		addressRepository.save(Address.from(strippedAddress, INACTIVE, getCustomer));

		return GetAllAddressResponse.of(addressRepository.findAddressResponseByCustomerId(getCustomer.getId()));
	}

	@Transactional
	public GetAllAddressResponse updateAddress(Long addressId, String newAddress, CustomUserDetails currentUser) {
		String strippedAddress = newAddress.strip();

		Address getAddress = getAddressOrElseThrow(addressId);
		if (getAddress.getAddress().equals(strippedAddress)) {
			throw new BaseException(ADDRESS_SAME_AS_OLD);
		}

		getAddress.updateAddress(strippedAddress);

		return GetAllAddressResponse.of(
			addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()));
	}

	@Transactional(readOnly = true)
	public GetAllAddressResponse getAllAddressResponse(CustomUserDetails currentUser) {
		return GetAllAddressResponse.of(
			addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()));
	}

	@Transactional
	public GetAllAddressResponse updateAddressStatus(Long addressId, CustomUserDetails currentUser) {
		Address getAddress = getAddressOrElseThrow(addressId);
		if (getAddress.getStatus() == INACTIVE) {
			// 다른 모든 주소를 비활성화
			addressRepository.findAllByCustomerId(currentUser.getUserInfo().getId())
				.forEach(address -> address.updateStatus(INACTIVE));

			// 요청된 주소를 활성화로 변경
			getAddress.updateStatus(ACTIVE);
		} else {
			// 이미 ACTIVE 상태일 경우 예외 처리
			throw new BaseException(ADDRESS_STATUS_IS_ALREADY_ACTIVE);
		}

		boolean noActiveStatus = addressRepository.findAllByCustomerId(currentUser.getUserInfo().getId())
			.stream()
			.noneMatch(address -> address.getStatus() == ACTIVE);

		// 최소 하나의 ACTIVE 상태가 없으면 예외 발생
		if (noActiveStatus) {
			throw new BaseException(NO_ACTIVE_ADDRESS);
		}

		return GetAllAddressResponse.of(
			addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()));
	}

	@Transactional
	public GetAllAddressResponse deleteAddress(Long addressId, CustomUserDetails currentUser) {
		Address getAddress = getAddressOrElseThrow(addressId);

		if (getAddress.getStatus() == ACTIVE) {
			throw new BaseException(DELETE_ADDRESS_FAILED);
		}

		addressRepository.delete(getAddress);

		return GetAllAddressResponse.of(
			addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()));
	}

	@Transactional
	public CustomerResponse updatePhoneNumber(String newPhoneNumber, CustomUserDetails currentUser) {
		Customer getCustomer = getCustomerOrElseThrow(currentUser.getUsername());

		if (customerRepository.existsByPhoneNumber(newPhoneNumber)) {
			throw new BaseException(PHONE_NUMBER_DUPLICATED);
		}

		if (getCustomer.getPhoneNumber().equals(newPhoneNumber)) {
			throw new BaseException(PHONE_NUMBER_SAME_AS_OLD);
		}

		getCustomer.changePhoneNumber(newPhoneNumber);

		return CustomerResponse.of(getCustomer);
	}

	@Transactional
	public CustomerResponse updatePassword(String oldPassword, String newPassword, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());

		if (!passwordEncoder.matches(oldPassword, getCustomer.getPassword())) {
			throw new BaseException(PASSWORD_NOT_MATCHED);
		}

		if (passwordEncoder.matches(newPassword, getCustomer.getPassword())) {
			throw new BaseException(PASSWORD_SAME_AS_OLD);
		}

		getCustomer.changePassword(passwordEncoder.encode(newPassword));
		return CustomerResponse.of(getCustomer);
	}

	@Transactional
	public CustomerResponse updateCustomerProfileUrl(String profileUrl, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());
		getCustomer.changeProfileUrl(profileUrl);

		return CustomerResponse.of(getCustomer);
	}

	@Transactional
	public void deleteCustomer(String password, String accessToken, CustomUserDetails currentUser) {
		if (!passwordEncoder.matches(password, currentUser.getPassword())) {
			throw new BaseException(PASSWORD_NOT_MATCHED);
		}

		customerRepository.softDeleteCustomer();

		String email = currentUser.getUsername();
		RefreshToken refreshToken = refreshTokenRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(INVALID_TOKEN));

		if (refreshTokenRepository.getKey(email) != null && tokenProvider.getSubject(refreshToken.getRefreshToken())
			.equals(email)) {
			refreshTokenRepository.delete(email);
		}

		String accessTokenWithoutBearer = tokenProvider.substringToken(accessToken);
		long expiration = tokenProvider.extractClaims(accessTokenWithoutBearer).getExpiration().getTime();

		refreshTokenRepository.addBlacklist(accessToken, expiration);
	}

	private Customer getCustomerOrElseThrow(String email) {
		return customerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	private Customer getActiveCustomerByEmail(String email) {
		return customerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	private Address getAddressOrElseThrow(Long addressId) {
		return addressRepository.findById(addressId)
			.orElseThrow(() -> new BaseException(ADDRESS_NOT_FOUND));
	}
}
