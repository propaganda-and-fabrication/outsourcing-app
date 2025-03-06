package com.outsourcing.domain.user.controller;

import static com.outsourcing.common.constant.Const.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.dto.request.DeleteUserRequest;
import com.outsourcing.domain.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/v1/users")
	public Response<Void> deleteUser(
		@Valid @RequestBody DeleteUserRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		HttpServletRequest httpServletRequest
	) {
		String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
		userService.deleteUser(request.getPassword(), accessToken, request.getRefreshToken(), currentUser);
		return Response.of(null);
	}
}
