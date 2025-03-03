package com.outsourcing.domain.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.user.dto.request.DeleteUserRequest;
import com.outsourcing.domain.user.dto.request.UpdatePasswordRequest;
import com.outsourcing.domain.user.dto.request.UpdatePhoneNumberRequest;
import com.outsourcing.domain.user.dto.request.customer.AddAddressRequest;
import com.outsourcing.domain.user.dto.request.customer.UpdateAddressRequest;
import com.outsourcing.domain.user.dto.request.customer.UpdateNicknameRequest;
import com.outsourcing.domain.user.dto.request.customer.UpdateProfileUrlRequest;
import com.outsourcing.domain.user.dto.response.CustomerResponse;
import com.outsourcing.domain.user.dto.response.GetAllAddressResponse;
import com.outsourcing.domain.user.service.CustomerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerService customerService;

	@GetMapping("/v1/customers/me")
	public Response<CustomerResponse> getCustomerProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
		CustomerResponse response = customerService.getCustomerProfile(currentUser);
		return Response.of(response, "Customer 프로필 조회 성공");
	}

	@PatchMapping("/v1/customers/me/nickname")
	public Response<CustomerResponse> updateNickname(@Valid @RequestBody UpdateNicknameRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		CustomerResponse response = customerService.updateNickname(request.getChangeNickname(), currentUser);
		return Response.of(response, "Customer 닉네임 수정 성공");
	}

	@PostMapping("/v1/customers/me/addresses")
	public Response<GetAllAddressResponse> addAddress(@Valid @RequestBody AddAddressRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		GetAllAddressResponse response = customerService.addAddress(request.getAddress(), currentUser);
		return Response.of(response, "Customer 주소 등록 성공");
	}

	@PatchMapping("/v1/customers/me/address/{addressId}")
	public Response<GetAllAddressResponse> updateAddress(
		@PathVariable Long addressId, @Valid @RequestBody UpdateAddressRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		GetAllAddressResponse response = customerService.updateAddress(addressId, request.getNewAddress(), currentUser);
		return Response.of(response, "Customer 주소 수정 성공");
	}

	@PatchMapping("/v1/customers/me/addresses/{addressId}/status")
	public Response<GetAllAddressResponse> updateAddressStatus(@PathVariable Long addressId,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		GetAllAddressResponse response = customerService.updateAddressStatus(addressId, currentUser);
		return Response.of(response, "Customer 주소 선택 성공");
	}

	@GetMapping("/v1/customers/me/addresses")
	public Response<GetAllAddressResponse> getAllAddresses(@AuthenticationPrincipal CustomUserDetails currentUser) {
		GetAllAddressResponse response = customerService.getAllAddresses(currentUser);
		return Response.of(response, "Customer 주소 전체 조회 성공");
	}

	@DeleteMapping("/v1/customers/me/addresses/{addressId}")
	public Response<GetAllAddressResponse> deleteAddress(@PathVariable Long addressId,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		GetAllAddressResponse response = customerService.deleteAddress(addressId, currentUser);
		return Response.of(response, "Customer 주소 삭제 성공");

	}

	@PatchMapping("/v1/customers/me/phone-number")
	public Response<CustomerResponse> updatePhoneNumber(@Valid @RequestBody UpdatePhoneNumberRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		CustomerResponse response = customerService.updatePhoneNumber(request.getNewPhoneNumber(), currentUser);
		return Response.of(response, "Customer 휴대폰 번호 수정 성공");
	}

	@PatchMapping("/v1/customers/me/password")
	public Response<CustomerResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		CustomerResponse response = customerService.updatePassword(request.getOldPassword(),
			request.getNewPassword(), currentUser);
		return Response.of(response, "Customer 비밀번호 수정 성공");
	}

	//TODO: 파일 시스템 완료 후 테스트 예정
	@PatchMapping("/v1/customers/me/profile-image")
	public Response<CustomerResponse> updateCustomerProfileUrl(@Valid @RequestBody UpdateProfileUrlRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {

		CustomerResponse response = customerService.updateCustomerProfileUrl(request.getNewProfileUrl(), currentUser);
		return Response.of(response, "Customer 프로필 이미지 수정 성공");
	}

	@PostMapping("/v1/customers/me/delete")
	public Response<Void> deleteCustomer(@Valid @RequestBody DeleteUserRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser, HttpServletRequest httpServletRequest) {

		String accessToken = httpServletRequest.getHeader("Authorization");
		customerService.deleteCustomer(request.getPassword(), accessToken, currentUser);
		return Response.of(null, "Customer 탈퇴 성공");
	}
}
