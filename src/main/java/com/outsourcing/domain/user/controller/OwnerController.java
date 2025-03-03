package com.outsourcing.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.dto.request.DeleteUserRequest;
import com.outsourcing.domain.user.dto.request.UpdatePasswordRequest;
import com.outsourcing.domain.user.dto.request.UpdatePhoneNumberRequest;
import com.outsourcing.domain.user.dto.response.OwnerResponse;
import com.outsourcing.domain.user.service.OwnerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OwnerController {

	private final OwnerService ownerService;

	@GetMapping("/v1/owners/me")
	public Response<OwnerResponse> getOwnerProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
		OwnerResponse response = ownerService.getOwnerProfile(currentUser);
		return Response.of(response, "Owner 프로필 조회 성공");
	}

	@PatchMapping("/v1/owners/me/phone-number")
	public Response<OwnerResponse> updatePhoneNumber(@Valid @RequestBody UpdatePhoneNumberRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		OwnerResponse response = ownerService.updatePhoneNumber(request.getNewPhoneNumber(), currentUser);
		return Response.of(response, "Owner 휴대폰 번호 수정 성공");
	}

	@PatchMapping("/v1/owners/me/password")
	public Response<OwnerResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		OwnerResponse response = ownerService.updatePassword(request.getOldPassword(),
			request.getNewPassword(), currentUser);
		return Response.of(response, "Owner 비밀번호 수정 성공");
	}

	@PostMapping("/v1/owners/me/delete")
	public Response<Void> deleteOwner(@Valid @RequestBody DeleteUserRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser, HttpServletRequest httpServletRequest) {

		String accessToken = httpServletRequest.getHeader("Authorization");
		ownerService.deleteOwner(request.getPassword(), accessToken, currentUser);
		return Response.of(null, "Owner 탈퇴 성공");
	}
}
