package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.AddressStatus.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
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
	public CustomerResponse updatePhoneNumber(String newPhoneNumber, CustomUserDetails currentUser) {
		Customer getCustomer = getCustomerOrElseThrow(currentUser.getUsername());

		// 휴대폰 번호가 이미 존재함
		if (customerRepository.existsByPhoneNumber(newPhoneNumber)) {
			throw new BaseException(PHONE_NUMBER_DUPLICATED);
		}

		// 이전 전화번호와 바꾸려는 전화번호가 동일
		if (getCustomer.getPhoneNumber().equals(newPhoneNumber)) {
			throw new BaseException(PHONE_NUMBER_SAME_AS_OLD);
		}

		getCustomer.changePhoneNumber(newPhoneNumber);

		return CustomerResponse.of(getCustomer);
	}

	@Transactional
	public CustomerResponse updatePassword(String oldPassword, String newPassword, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());

		// 비밀번호 불일치
		if (!passwordEncoder.matches(oldPassword, getCustomer.getPassword())) {
			throw new BaseException(PASSWORD_NOT_MATCHED);
		}

		// 이전 비밀번호와 바꾸려는 비밀번호가 동일
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
	public GetAllAddressResponse addAddress(String address, CustomUserDetails currentUser) {
		Customer getCustomer = getActiveCustomerByEmail(currentUser.getUsername());

		// 주소 앞뒤 공백 제거
		String strippedAddress = address.strip();
		addressRepository.save(Address.from(strippedAddress, INACTIVE, getCustomer));

		return GetAllAddressResponse.of(addressRepository.findAddressResponseByCustomerId(getCustomer.getId()));
	}

	@Transactional
	public GetAllAddressResponse updateAddress(Long addressId, String newAddress, CustomUserDetails currentUser) {
		Address getAddress = getAddressOrElseThrow(addressId);

		// 주소에 등록된 CustomerId와 로그인한 사용자의 id 값이 다를 경우 예외 발생
		validateAddressOwnership(getAddress.getCustomer().getId(), currentUser.getUserInfo().getId());

		// 주소 앞뒤 공백 제거
		String strippedAddress = newAddress.strip();
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

		// 주소에 등록된 CustomerId와 로그인한 사용자의 id 값이 다를 경우 예외 발생
		validateAddressOwnership(getAddress.getCustomer().getId(), currentUser.getUserInfo().getId());

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

		// 최소 하나의 ACTIVE 상태가 없으면 예외 발생
		boolean noActiveStatus = addressRepository.findAllByCustomerId(currentUser.getUserInfo().getId())
			.stream()
			.noneMatch(address -> address.getStatus() == ACTIVE);

		if (noActiveStatus) {
			throw new BaseException(NO_ACTIVE_ADDRESS);
		}

		return GetAllAddressResponse.of(
			addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()));
	}

	@Transactional
	public GetAllAddressResponse deleteAddress(Long addressId, CustomUserDetails currentUser) {
		Address getAddress = getAddressOrElseThrow(addressId);

		// 주소에 등록된 CustomerId와 로그인한 사용자의 id 값이 다를 경우 예외 발생
		validateAddressOwnership(getAddress.getCustomer().getId(), currentUser.getUserInfo().getId());

		if (getAddress.getStatus() == ACTIVE) {
			throw new BaseException(DELETE_ADDRESS_FAILED);
		}

		addressRepository.delete(getAddress);

		return GetAllAddressResponse.of(
			addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()));
	}

	// 탈퇴한 사용자까지 조회
	private Customer getCustomerOrElseThrow(String email) {
		return customerRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	// 탈퇴한 사용자 제외 조회
	private Customer getActiveCustomerByEmail(String email) {
		return customerRepository.findByEmailAndDeletedAt(email)
			.orElseThrow(() -> new BaseException(USER_NOT_FOUND));
	}

	private Address getAddressOrElseThrow(Long addressId) {
		return addressRepository.findById(addressId)
			.orElseThrow(() -> new BaseException(ADDRESS_NOT_FOUND));
	}

	// 로그인한 사용자가 다른 customer의 주소를 바꾸려고 하는지 확인
	private void validateAddressOwnership(Long customerId, Long currentUserId) {
		if (!customerId.equals(currentUserId)) {
			throw new BaseException(ADDRESS_ACCESS_DENIED);
		}
	}
}
