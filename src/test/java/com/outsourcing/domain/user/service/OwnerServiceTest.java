package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.UserRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import com.outsourcing.domain.user.dto.UserInfo;
import com.outsourcing.domain.user.dto.response.OwnerResponse;
import com.outsourcing.domain.user.entity.Owner;
import com.outsourcing.domain.user.repository.OwnerRepository;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

	@Mock
	OwnerRepository ownerRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	OwnerService ownerService;

	@Test
	public void Owner_프로필_조회_성공() {
		// given
		Owner owner = new Owner("a@a.com", "password", "owner", "0000", OWNER);
		ReflectionTestUtils.setField(owner, "id", 1L);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(owner.getId(), owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getDeletedAt()));

		given(ownerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(owner));

		// when
		OwnerResponse response = ownerService.getOwnerProfile(currentUser);

		// then
		assertNotNull(response);
		assertEquals(owner.getId(), response.getId());
	}

	@Test
	public void 전화번호_수정_성공() {
		// given
		Owner owner = new Owner("a@a.com", "password", "owner", "0000", OWNER);
		String newPhoneNumber = "1234";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(owner.getId(), owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getDeletedAt()));

		given(ownerRepository.findByEmail(anyString())).willReturn(Optional.of(owner));
		given(ownerRepository.existsByPhoneNumber(anyString())).willReturn(false);

		// when
		OwnerResponse response = ownerService.updatePhoneNumber(newPhoneNumber, currentUser);

		// then
		assertNotNull(response);
		assertEquals(newPhoneNumber, response.getPhoneNumber());
	}

	@Test
	public void 이미_존재하는_전화번호_실패() {
		// given
		Owner owner = new Owner("a@a.com", "password", "owner", "0000", OWNER);
		String newPhoneNumber = "1234";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(owner.getId(), owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getDeletedAt()));

		given(ownerRepository.findByEmail(anyString())).willReturn(Optional.of(owner));
		given(ownerRepository.existsByPhoneNumber(anyString())).willReturn(true);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> ownerService.updatePhoneNumber(newPhoneNumber, currentUser));

		// then
		assertEquals(PHONE_NUMBER_DUPLICATED, exception.getErrorCode());
	}

	@Test
	public void 이전과_동일한_전화번호_실패() {
		// given
		Owner owner = new Owner("a@a.com", "password", "owner", "0000", OWNER);
		String newPhoneNumber = "0000";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(owner.getId(), owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getDeletedAt()));

		given(ownerRepository.findByEmail(anyString())).willReturn(Optional.of(owner));
		given(ownerRepository.existsByPhoneNumber(anyString())).willReturn(false);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> ownerService.updatePhoneNumber(newPhoneNumber, currentUser));

		// then
		assertEquals(PHONE_NUMBER_SAME_AS_OLD, exception.getErrorCode());
	}

	@Test
	public void 비밀번호_수정_성공() {
		// given
		Owner owner = new Owner("a@a.com", "password", "owner", "0000", OWNER);
		String newRawPassword = "newRawPassword";
		String encodedPassword = "newPassword";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(owner.getId(), owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getDeletedAt()));

		given(ownerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(owner));
		given(passwordEncoder.matches("password", owner.getPassword())).willReturn(true);
		given(passwordEncoder.matches(newRawPassword, owner.getPassword())).willReturn(false);
		given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);

		// when
		OwnerResponse response = ownerService.updatePassword("password", newRawPassword, currentUser);

		// then
		assertNotNull(response);
		assertEquals(encodedPassword, owner.getPassword());
	}

	@Test
	public void 비밀번호_불일치_실패() {
		// given
		Owner owner = new Owner("a@a.com", "password", "owner", "0000", OWNER);
		String newRawPassword = "newRawPassword";
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(owner.getId(), owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getDeletedAt()));

		given(ownerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(owner));
		given(passwordEncoder.matches(anyString(), eq(owner.getPassword()))).willReturn(false);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> ownerService.updatePassword("1234", newRawPassword, currentUser));

		// then
		assertEquals(PASSWORD_NOT_MATCHED, exception.getErrorCode());
	}

	@Test
	public void 이전_비밀번호와_동일_실패() {
		// given
		Owner owner = new Owner("a@a.com", "password", "owner", "0000", OWNER);
		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(owner.getId(), owner.getEmail(), owner.getPassword(), owner.getRole(), owner.getDeletedAt()));

		given(ownerRepository.findByEmailAndDeletedAt(anyString())).willReturn(Optional.of(owner));
		given(passwordEncoder.matches("password", owner.getPassword()))
			.willReturn(true)    // 비밀번호 일치
			.willReturn(true);    // 이전 비밀번호와 일치

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> ownerService.updatePassword("password", "password", currentUser));

		// then
		assertEquals(PASSWORD_SAME_AS_OLD, exception.getErrorCode());
	}

	@Test
	public void 탈퇴한_사용자_포함_조회_실패() {
		// given
		String email = "a@a.com";
		given(ownerRepository.findByEmail(email)).willReturn(Optional.empty()); // Mocking 설정

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> ReflectionTestUtils.invokeMethod(ownerService, "getOwnerOrElseThrow", email));

		// then
		assertEquals(USER_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	public void 탈퇴한_사용자_미포함_조회_실패() {
		// given
		String email = "a@a.com";
		given(ownerRepository.findByEmailAndDeletedAt(email)).willReturn(Optional.empty()); // Mocking 설정

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> ReflectionTestUtils.invokeMethod(ownerService, "getActiveOwnerByEmail", email));

		// then
		assertEquals(USER_NOT_FOUND, exception.getErrorCode());
	}
}