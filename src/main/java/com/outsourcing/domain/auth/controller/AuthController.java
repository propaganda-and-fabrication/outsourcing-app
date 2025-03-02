package com.outsourcing.domain.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.dto.request.CustomerSignUpRequest;
import com.outsourcing.domain.auth.dto.request.LogoutRequest;
import com.outsourcing.domain.auth.dto.request.OwnerSignUpRequest;
import com.outsourcing.domain.auth.dto.request.SignInRequest;
import com.outsourcing.domain.auth.dto.request.TokenReissueRequest;
import com.outsourcing.domain.auth.dto.response.TokenResponse;
import com.outsourcing.domain.auth.service.AuthService;
import com.outsourcing.domain.auth.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/v1/auth/customers")
	public Response<TokenResponse> signUpCustomer(@Valid @RequestBody CustomerSignUpRequest request) {
		TokenResponse tokenResponse = authService.signUp(request.getEmail(), request.getPassword(), request.getName(),
			request.getPhoneNumber(), request.getUserRole());
		return Response.of(tokenResponse, "Customer 회원가입 성공");
	}

	@PostMapping("/v1/auth/owners")
	public Response<TokenResponse> signUpOwners(@Valid @RequestBody OwnerSignUpRequest request) {
		TokenResponse tokenResponse = authService.signUp(request.getEmail(), request.getPassword(), request.getName(),
			request.getPhoneNumber(), request.getUserRole());
		return Response.of(tokenResponse, "Owner 회원가입 성공");
	}

	@PostMapping("/v1/auth/sign-in")
	public Response<TokenResponse> signIn(@Valid @RequestBody SignInRequest request) {
		TokenResponse tokenResponse = authService.signIn(request.getEmail(), request.getPassword());
		return Response.of(tokenResponse, "로그인 성공");
	}

	@PostMapping("/v1/auth/reissue")
	public Response<TokenResponse> tokenReissue(@Valid @RequestBody TokenReissueRequest request) {
		TokenResponse tokenResponse = authService.tokenReissue(request.getRefreshToken());
		return Response.of(tokenResponse, "토큰 재발급 성공");
	}

	@PostMapping("/v1/auth/logout")
	public Response<Void> logout(@Valid @RequestBody LogoutRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		authService.logout(request.getAccessToken(), request.getRefreshToken(), userDetails);
		return Response.of(null, "로그아웃 되었습니다.");
	}
}
