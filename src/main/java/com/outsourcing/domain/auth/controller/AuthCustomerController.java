package com.outsourcing.domain.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.dto.request.CustomerSignUpRequest;
import com.outsourcing.domain.auth.dto.request.SignInRequest;
import com.outsourcing.domain.auth.dto.request.TokenReissueRequest;
import com.outsourcing.domain.auth.dto.response.TokenResponse;
import com.outsourcing.domain.auth.service.AuthCustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthCustomerController {

	private final AuthCustomerService customerService;

	/* Customer Auth API  */
	@PostMapping("/v1/auth/customers")
	public Response<TokenResponse> signUpCustomer(@Valid @RequestBody CustomerSignUpRequest request) {
		TokenResponse tokenResponse = customerService.signUpCustomer(request.getEmail(), request.getPassword(),
			request.getName(), request.getPhoneNumber(), request.getAddress());
		return Response.of(tokenResponse);
	}

	@PostMapping("/v1/auth/customers/sign-in")
	public Response<TokenResponse> signInCustomer(@Valid @RequestBody SignInRequest request) {
		TokenResponse tokenResponse = customerService.signInCustomer(request.getEmail(), request.getPassword());
		return Response.of(tokenResponse);
	}

	@PostMapping("/v1/auth/customers/reissue")
	public Response<TokenResponse> customerTokenReissue(@Valid @RequestBody TokenReissueRequest request) {
		TokenResponse tokenResponse = customerService.customerTokenReissue(request.getRefreshToken());
		return Response.of(tokenResponse);
	}
}
