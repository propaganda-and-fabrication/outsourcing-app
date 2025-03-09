package com.outsourcing.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.dto.request.OwnerSignUpRequest;
import com.outsourcing.domain.auth.dto.request.SignInRequest;
import com.outsourcing.domain.auth.dto.request.TokenReissueRequest;
import com.outsourcing.domain.auth.dto.response.TokenResponse;
import com.outsourcing.domain.auth.service.AuthOwnerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthOwnerController {

	private final AuthOwnerService ownerService;

	/* Owner Auth API  */
	@PostMapping("/v1/auth/owners")
	public Response<TokenResponse> signUpOwner(@Valid @RequestBody OwnerSignUpRequest request) {
		TokenResponse tokenResponse = ownerService.signUpOwner(request.getEmail(), request.getPassword(),
			request.getName(), request.getPhoneNumber());
		return Response.of(tokenResponse);
	}

	@PostMapping("/v1/auth/owners/sign-in")
	public Response<TokenResponse> signInOwner(@Valid @RequestBody SignInRequest request) {
		TokenResponse tokenResponse = ownerService.signInOwner(request.getEmail(), request.getPassword());
		return Response.of(tokenResponse);
	}

	@PostMapping("/v1/auth/owners/reissue")
	public Response<TokenResponse> ownerTokenReissue(@Valid @RequestBody TokenReissueRequest request) {
		TokenResponse tokenResponse = ownerService.ownerTokenReissue(request.getRefreshToken());
		return Response.of(tokenResponse);
	}
}
