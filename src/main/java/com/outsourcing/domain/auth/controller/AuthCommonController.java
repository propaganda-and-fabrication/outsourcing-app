package com.outsourcing.domain.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.dto.request.LogoutRequest;
import com.outsourcing.domain.auth.service.AuthCommonService;
import com.outsourcing.domain.auth.service.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthCommonController {

	private final AuthCommonService authCommonService;

	/* Common Auth API */
	@PostMapping("/v1/auth/logout")
	public Response<Void> logout(@Valid @RequestBody LogoutRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {
		authCommonService.logout(request.getAccessToken(), request.getRefreshToken(), currentUser);
		return Response.of(null);
	}
}
