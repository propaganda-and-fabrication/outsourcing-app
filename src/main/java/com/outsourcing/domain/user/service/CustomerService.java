package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.AddressStatus.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.entity.RefreshToken;
import com.outsourcing.domain.auth.repository.RefreshTokenRepositoryImpl;
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
	private final RefreshTokenRepositoryImpl refreshTokenRepository;
	private final JwtTokenProvider tokenProvider;

	@Transactional(readOnly = true)
	public CustomerResponse getUserProfile(CustomUserDetails currentUser) {
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

		Address newAddress = Address.from(address);
		getCustomer.addAddress(newAddress);
		customerRepository.save(getCustomer);    // address와 동시 저장을 위해 사용

		return GetAllAddressResponse.of(addressRepository.findAllByCustomerId(getCustomer.getId()));
	}

	@Transactional
	public GetAllAddressResponse updateAddress(Long addressId, String newAddress, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());

		Address getAddress = getAddressOrElseThrow(addressId);
		if (getAddress.getAddress().equals(newAddress)) {
			throw new BaseException(ADDRESS_SAME_AS_OLD);
		}
		getAddress.updateAddress(newAddress);
		customerRepository.save(getCustomer);

		return GetAllAddressResponse.of(addressRepository.findAllByCustomerId(getCustomer.getId()));
	}

	@Transactional(readOnly = true)
	public GetAllAddressResponse getAllAddresses(CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());
		return GetAllAddressResponse.of(getCustomer.getAddresses());
	}

	@Transactional
	public GetAllAddressResponse updateAddressStatus(Long addressId, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());

		Address getAddress = getAddressOrElseThrow(addressId);
		if (getAddress.getStatus() == INACTIVE) {
			// 다른 모든 주소를 비활성화
			addressRepository.findAllByCustomerId(getCustomer.getId())
				.forEach(address -> address.updateStatus(INACTIVE));

			// 요청된 주소를 활성화로 변경
			getAddress.updateStatus(ACTIVE);
		} else {
			// 이미 ACTIVE 상태일 경우 예외 처리
			throw new BaseException(ADDRESS_STATUS_IS_ALREADY_ACTIVE);
		}

		boolean noActiveStatus = addressRepository.findAllByCustomerId(getCustomer.getId())
			.stream()
			.noneMatch(address -> address.getStatus() == ACTIVE);

		// 최소 하나의 ACTIVE 상태가 없으면 예외 발생
		if (noActiveStatus) {
			throw new BaseException(NO_ACTIVE_ADDRESS);
		}

		return GetAllAddressResponse.of(getCustomer.getAddresses());
	}

	@Transactional
	public GetAllAddressResponse deleteAddress(Long addressId, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());
		Address getAddress = getAddressOrElseThrow(addressId);

		if (getAddress.getStatus() == ACTIVE) {
			throw new BaseException(DELETE_ADDRESS_FAILED);
		}

		getCustomer.removeAddress(getAddress);    // orphanRemoval에 의해 자동 삭제

		return GetAllAddressResponse.of(getCustomer.getAddresses());
	}

	@Transactional
	public CustomerResponse updatePhoneNumber(String newPhoneNumber,
		CustomUserDetails currentUser) {
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
	public CustomerResponse updateProfileUrl(String profileUrl, CustomUserDetails currentUser) {
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
