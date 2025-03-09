package com.outsourcing.domain.user.service;

import static com.outsourcing.common.exception.ErrorCode.*;
import static com.outsourcing.domain.user.enums.UserRole.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.util.jwt.JwtTokenProvider;
import com.outsourcing.domain.auth.repository.RefreshTokenRepository;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.dto.UserInfo;
import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	RefreshTokenRepository refreshTokenRepository;

	@Mock
	JwtTokenProvider tokenProvider;

	@InjectMocks
	UserService userService;

	@Test
	public void 유저_탈퇴_성공() {
		// given
		User user = new User("a@a.com", "password", "kim", "000", CUSTOMER);
		ReflectionTestUtils.setField(user, "id", 1L);

		String accessToken = "accessToken";
		String subToken = "subToken";
		String refreshToken = "refreshToken";

		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getDeletedAt())
		);

		Claims mockClaims = mock(Claims.class);

		given(mockClaims.getExpiration()).willReturn(new Date(System.currentTimeMillis() + 1000L));
		given(passwordEncoder.matches(anyString(), eq(user.getPassword()))).willReturn(true);
		given(refreshTokenRepository.getValueByKey(currentUser.getUsername())).willReturn(refreshToken);
		given(tokenProvider.getSubject(refreshToken)).willReturn(currentUser.getUsername());

		doNothing().when(refreshTokenRepository).delete(currentUser.getUsername());
		doNothing().when(userRepository).softDeleteUser();

		given(tokenProvider.substringToken(accessToken)).willReturn(subToken);
		given(tokenProvider.extractClaims(subToken)).willReturn(mockClaims);
		doNothing().when(refreshTokenRepository).addBlacklist(eq(accessToken), anyLong());

		// when
		userService.deleteUser(user.getPassword(), accessToken, refreshToken, currentUser);

		// then
		then(passwordEncoder).should(times(1)).matches(anyString(), eq(user.getPassword()));
		then(refreshTokenRepository).should(times(1)).delete(currentUser.getUsername());
		then(userRepository).should(times(1)).softDeleteUser();
	}

	@Test
	public void 비밀번호_불일치_실패() {
		// given
		User user = new User("a@a.com", "password", "kim", "000", CUSTOMER);
		ReflectionTestUtils.setField(user, "id", 1L);

		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getDeletedAt())
		);

		given(passwordEncoder.matches(anyString(), eq(user.getPassword()))).willReturn(false);

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> userService.deleteUser("password", "accessToken", "refreshToken", currentUser));

		// then
		assertEquals(PASSWORD_NOT_MATCHED, exception.getErrorCode());
	}

	@Test
	public void refreshToken_불일치로_인한_실패() {
		// given
		User user = new User("a@a.com", "password", "kim", "000", CUSTOMER);
		ReflectionTestUtils.setField(user, "id", 1L);

		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getDeletedAt())
		);

		String refreshToken = "refreshToken";

		// Mocking
		given(passwordEncoder.matches(anyString(), eq(user.getPassword()))).willReturn(true);
		given(refreshTokenRepository.getValueByKey(currentUser.getUsername())).willReturn("different refreshToken");

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> userService.deleteUser("password", "accessToken", refreshToken, currentUser));

		// then
		assertEquals(INVALID_TOKEN, exception.getErrorCode());
	}

	@Test
	public void subject_불일치로_인한_실패() {
		// given
		User user = new User("a@a.com", "password", "kim", "000", CUSTOMER);
		ReflectionTestUtils.setField(user, "id", 1L);

		CustomUserDetails currentUser = new CustomUserDetails(
			new UserInfo(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.getDeletedAt())
		);

		String refreshToken = "invalidRefreshToken";

		// Mocking
		given(passwordEncoder.matches(anyString(), eq(user.getPassword()))).willReturn(true);
		given(refreshTokenRepository.getValueByKey(currentUser.getUsername())).willReturn(refreshToken);
		given(tokenProvider.getSubject(refreshToken)).willReturn("different email");

		// when
		BaseException exception = assertThrows(BaseException.class,
			() -> userService.deleteUser("password", "accessToken", refreshToken, currentUser));

		// then
		assertEquals(INVALID_TOKEN, exception.getErrorCode());
	}

}