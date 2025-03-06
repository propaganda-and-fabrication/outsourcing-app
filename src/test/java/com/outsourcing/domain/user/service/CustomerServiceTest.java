package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.AddressStatus.*;
import static com.outsourcing.domain.user.enums.UserRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.dto.AddressDto;
import com.outsourcing.domain.user.dto.UserInfo;
import com.outsourcing.domain.user.dto.response.CustomerResponse;
import com.outsourcing.domain.user.dto.response.GetAllAddressResponse;
import com.outsourcing.domain.user.entity.Address;
import com.outsourcing.domain.user.entity.Customer;
import com.outsourcing.domain.user.repository.AddressRepository;
import com.outsourcing.domain.user.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	CustomerRepository customerRepository;

	@Mock
	AddressRepository addressRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	CustomerService customerService;

	@Test
	public void Customer_프로필_조회_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(mockCustomer));

		// when
		CustomerResponse response = customerService.getCustomerProfile(currentUser);

		// then
		assertNotNull(response);
		assertEquals(mockCustomer.getId(), response.getId());
	}

	@Test
	public void 닉네임_변경_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		String newNickname = "nickname";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.existsByNickname(anyString())).willReturn(false);
		given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(mockCustomer));

		// when
		CustomerResponse response = customerService.updateNickname(newNickname, currentUser);

		// then
		assertNotNull(response);
		assertEquals(mockCustomer.getNickname(), response.getNickname());
	}

	@Test
	public void 닉네임_중복으로_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		String newNickname = "nickname";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.existsByNickname(anyString())).willReturn(true);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updateNickname(newNickname, currentUser));

		// then
		assertEquals(NICKNAME_DUPLICATED, exception.getErrorCode());
	}

	@Test
	public void 이전과_같은_닉네임_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		ReflectionTestUtils.setField(mockCustomer, "nickname", "nickname");
		String newNickname = "nickname";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.existsByNickname(anyString())).willReturn(false);
		given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(mockCustomer));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updateNickname(newNickname, currentUser));

		// then
		assertEquals(NICKNAME_SAME_AS_OLD, exception.getErrorCode());
	}

	@Test
	public void 전화번호_변경_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		ReflectionTestUtils.setField(mockCustomer, "nickname", "nickname");
		String newPhoneNumber = "111";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.existsByPhoneNumber(anyString())).willReturn(false);
		given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(mockCustomer));

		// when
		CustomerResponse response = customerService.updatePhoneNumber(newPhoneNumber, currentUser);

		// then
		assertEquals(mockCustomer.getPhoneNumber(), response.getPhoneNumber());
	}

	@Test
	public void 이미_존재하는_전화번호_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		ReflectionTestUtils.setField(mockCustomer, "nickname", "nickname");
		String newPhoneNumber = "111";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.existsByPhoneNumber(anyString())).willReturn(true);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updatePhoneNumber(newPhoneNumber, currentUser));

		// then
		assertEquals(PHONE_NUMBER_DUPLICATED, exception.getErrorCode());
	}

	@Test
	public void 이전_전화번호와_동일_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		ReflectionTestUtils.setField(mockCustomer, "nickname", "nickname");
		String newPhoneNumber = "000";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.existsByPhoneNumber(anyString())).willReturn(false);
		given(customerRepository.findByEmail(anyString())).willReturn(Optional.of(mockCustomer));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updatePhoneNumber(newPhoneNumber, currentUser));

		// then
		assertEquals(PHONE_NUMBER_SAME_AS_OLD, exception.getErrorCode());
	}

	@Test
	public void 비밀번호_변경_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		String newPassword = "newPassword";
		String encodedPassword = "encodedPassword";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(mockCustomer));
		given(passwordEncoder.matches(anyString(), eq(mockCustomer.getPassword())))
			.willReturn(true)    // 비밀번호 일치
			.willReturn(false);    // 이전 비밀번호와 동일하지 않음
		given(passwordEncoder.encode(newPassword)).willReturn(encodedPassword);

		// when
		CustomerResponse response = customerService.updatePassword("oldPassword", newPassword, currentUser);

		// then
		assertNotNull(response);
		assertEquals(encodedPassword, mockCustomer.getPassword());
	}

	@Test
	public void 비밀번호_불일치_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		String newPassword = "newPassword";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(mockCustomer));
		given(passwordEncoder.matches(anyString(), eq(mockCustomer.getPassword()))).willReturn(false);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updatePassword("password", newPassword, currentUser));

		// then
		assertEquals(PASSWORD_NOT_MATCHED, exception.getErrorCode());
	}

	@Test
	public void 이전_비밀번호와_동일_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		String newPassword = "password";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(mockCustomer));
		given(passwordEncoder.matches(anyString(), eq(mockCustomer.getPassword())))
			.willReturn(true)
			.willReturn(true);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updatePassword("password", newPassword, currentUser));

		// then
		assertEquals(PASSWORD_SAME_AS_OLD, exception.getErrorCode());
	}

	@Test
	public void 프로필_이미지_수정_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		String profileUrl = "profileUrl";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(mockCustomer));

		// when
		CustomerResponse response = customerService.updateCustomerProfileUrl(profileUrl, currentUser);

		// then
		assertNotNull(response);
		assertEquals(profileUrl, response.getProfileUrl());
	}

	@Test
	public void 주소_등록_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		Address newAddress = Address.from("address", ACTIVE, mockCustomer);
		List<AddressDto> mockDto = List.of(AddressDto.from(newAddress));
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt()));

		given(customerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(mockCustomer));
		given(addressRepository.save(any(Address.class))).willReturn(newAddress);
		given(addressRepository.findAddressResponseByCustomerId(anyLong())).willReturn(mockDto);

		// when
		GetAllAddressResponse response = customerService.addAddress("address", currentUser);

		// then
		assertNotNull(response);
		assertEquals(1, response.getAddressResponses().size());
	}

	@Test
	public void 주소_수정_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress = Address.from("oldAddress", ACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress, "id", 1L);

		String newAddress = "newAddress";

		given(addressRepository.findById(mockAddress.getId())).willReturn(Optional.of(mockAddress));
		given(addressRepository.findAddressResponseByCustomerId(mockCustomer.getId()))
			.willAnswer(invocation -> {
				// mockAddress의 현재 상태를 기반으로 AddressDto 생성
				return List.of(AddressDto.from(mockAddress));
			});

		// when
		GetAllAddressResponse response = customerService.updateAddress(mockAddress.getId(), newAddress, currentUser);

		// then
		assertNotNull(response);
		assertEquals(1, response.getAddressResponses().size());
		assertEquals(newAddress, response.getAddressResponses().get(0).getAddress());
	}

	@Test
	public void 주소를_찾지_못해_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress = Address.from("oldAddress", ACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress, "id", 1L);

		given(addressRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updateAddress(mockAddress.getId(), mockAddress.getAddress(), currentUser));

		// then
		assertEquals(ADDRESS_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	public void 주소에_등록된_id와_로그인한_id가_달라서_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);

		Customer adCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(adCustomer, "id", 2L);

		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress = Address.from("oldAddress", ACTIVE, adCustomer);
		ReflectionTestUtils.setField(mockAddress, "id", 1L);

		given(addressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updateAddress(mockAddress.getId(), "newAddress", currentUser));

		// then
		assertEquals(ADDRESS_ACCESS_DENIED, exception.getErrorCode());
	}

	@Test
	public void 주소가_이전과_동일해서_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress = Address.from("address", ACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress, "id", 1L);

		String newAddress = "address";

		given(addressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updateAddress(mockAddress.getId(), newAddress, currentUser));

		// then
		assertEquals(ADDRESS_SAME_AS_OLD, exception.getErrorCode());
	}

	@Test
	public void 로그인한_유저의_모든_주소_조회_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress = Address.from("address", ACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress, "id", 1L);

		given(addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()))
			.willReturn(List.of(AddressDto.from(mockAddress)));

		// when
		GetAllAddressResponse response = customerService.getAllAddressResponse(currentUser);

		// then
		assertNotNull(response);
		assertEquals(1, response.getAddressResponses().size());
		assertEquals(mockAddress.getAddress(), response.getAddressResponses().get(0).getAddress());
	}

	@Test
	public void 배달_받을_주소로_설정_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress1 = Address.from("address", INACTIVE, mockCustomer);
		Address mockAddress2 = Address.from("address", ACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress1, "id", 1L);
		ReflectionTestUtils.setField(mockAddress2, "id", 2L);

		given(addressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress1));
		given(addressRepository.findAllByCustomerId(currentUser.getUserInfo().getId()))
			.willAnswer(invocation -> {
				mockAddress1.updateStatus(INACTIVE);
				mockAddress2.updateStatus(ACTIVE);
				return List.of(mockAddress1, mockAddress2);
			});
		given(addressRepository.findAddressResponseByCustomerId(currentUser.getUserInfo().getId()))
			.willReturn(List.of(AddressDto.from(mockAddress1), AddressDto.from(mockAddress2)));

		// when
		GetAllAddressResponse response = customerService.updateAddressStatus(mockAddress1.getId(), currentUser);

		// then
		assertNotNull(response);
		assertEquals(2, response.getAddressResponses().size());
		assertEquals(mockAddress1.getStatus(), response.getAddressResponses().get(0).getStatus());
		assertEquals(mockAddress2.getStatus(), response.getAddressResponses().get(1).getStatus());
	}

	@Test
	public void 이미_설정된_주소_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress1 = Address.from("address", ACTIVE, mockCustomer);
		Address mockAddress2 = Address.from("address", INACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress1, "id", 1L);
		ReflectionTestUtils.setField(mockAddress2, "id", 2L);

		given(addressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress1));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updateAddressStatus(mockAddress1.getId(), currentUser));

		// then
		assertEquals(ADDRESS_STATUS_IS_ALREADY_ACTIVE, exception.getErrorCode());
	}

	@Test
	public void 모든_주소의_상태가_INACTIVE_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress1 = Address.from("address", INACTIVE, mockCustomer);
		Address mockAddress2 = Address.from("address", INACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress1, "id", 1L);
		ReflectionTestUtils.setField(mockAddress2, "id", 2L);

		given(addressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress1));
		given(addressRepository.findAllByCustomerId(currentUser.getUserInfo().getId()))
			.willAnswer(invocation -> {
				mockAddress1.updateStatus(INACTIVE);
				mockAddress2.updateStatus(INACTIVE);
				return List.of(mockAddress1, mockAddress2);
			});

		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.updateAddressStatus(mockAddress1.getId(), currentUser));

		// then
		assertEquals(NO_ACTIVE_ADDRESS, exception.getErrorCode());
	}

	@Test
	public void 주소_삭제_성공() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress1 = Address.from("address", INACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress1, "id", 1L);

		given(addressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress1));

		// when
		GetAllAddressResponse response = customerService.deleteAddress(mockAddress1.getId(), currentUser);

		// then
		assertNotNull(response);
		assertEquals(0, response.getAddressResponses().size());
	}

	@Test
	public void ACTIVE_상태_주소_삭제_실패() {
		// given
		Customer mockCustomer = new Customer("a@a.com", "password", "name", "000", CUSTOMER);
		ReflectionTestUtils.setField(mockCustomer, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(mockCustomer.getId(), mockCustomer.getEmail(), mockCustomer.getPassword(),
				mockCustomer.getRole(), mockCustomer.getDeletedAt())
		);

		Address mockAddress1 = Address.from("address", ACTIVE, mockCustomer);
		ReflectionTestUtils.setField(mockAddress1, "id", 1L);

		given(addressRepository.findById(anyLong())).willReturn(Optional.of(mockAddress1));

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> customerService.deleteAddress(mockAddress1.getId(), currentUser));

		// then
		assertEquals(DELETE_ADDRESS_FAILED, exception.getErrorCode());
	}

}