package com.outsourcing.domain.user.controller;

import static com.outsourcing.common.constant.Const.*;

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

	/* Customer API */
	@GetMapping("/v1/customers/me")
	public Response<CustomerResponse> getCustomerProfile(@AuthenticationPrincipal CustomUserDetails currentUser) {
		CustomerResponse response = customerService.getCustomerProfile(currentUser);
		return Response.of(response);
	}

	@PatchMapping("/v1/customers/me/nickname")
	public Response<CustomerResponse> updateNickname(
		@Valid @RequestBody UpdateNicknameRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		CustomerResponse response = customerService.updateNickname(request.getChangeNickname(), currentUser);
		return Response.of(response);
	}

	@PatchMapping("/v1/customers/me/phone-number")
	public Response<CustomerResponse> updatePhoneNumber(
		@Valid @RequestBody UpdatePhoneNumberRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		CustomerResponse response = customerService.updatePhoneNumber(request.getNewPhoneNumber(), currentUser);
		return Response.of(response);
	}

	@PatchMapping("/v1/customers/me/password")
	public Response<CustomerResponse> updatePassword(
		@Valid @RequestBody UpdatePasswordRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		CustomerResponse response = customerService.updatePassword(request.getOldPassword(),
			request.getNewPassword(), currentUser);
		return Response.of(response);
	}

	@PatchMapping("/v1/customers/me/profile-image")
	public Response<CustomerResponse> updateCustomerProfileUrl(
		@Valid @RequestBody UpdateProfileUrlRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		CustomerResponse response = customerService.updateCustomerProfileUrl(request.getNewProfileUrl(), currentUser);
		return Response.of(response);
	}

	@PostMapping("/v1/customers/me/delete")
	public Response<Void> deleteCustomer(
		@Valid @RequestBody DeleteUserRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		HttpServletRequest httpServletRequest
	) {
		String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
		customerService.deleteCustomer(request.getPassword(), accessToken, request.getRefreshToken(), currentUser);
		return Response.of(null);
	}

	/* Address API */
	@PostMapping("/v1/customers/me/addresses")
	public Response<GetAllAddressResponse> addAddress(
		@Valid @RequestBody AddAddressRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		GetAllAddressResponse response = customerService.addAddress(request.getAddress(), currentUser);
		return Response.of(response);
	}

	@PatchMapping("/v1/customers/me/address/{addressId}")
	public Response<GetAllAddressResponse> updateAddress(
		@PathVariable Long addressId,
		@Valid @RequestBody UpdateAddressRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		GetAllAddressResponse response = customerService.updateAddress(addressId, request.getNewAddress(), currentUser);
		return Response.of(response);
	}

	@PatchMapping("/v1/customers/me/addresses/{addressId}/status")
	public Response<GetAllAddressResponse> updateAddressStatus(
		@PathVariable Long addressId,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		GetAllAddressResponse response = customerService.updateAddressStatus(addressId, currentUser);
		return Response.of(response);
	}

	@GetMapping("/v1/customers/me/addresses")
	public Response<GetAllAddressResponse> getAllAddresses(@AuthenticationPrincipal CustomUserDetails currentUser) {
		GetAllAddressResponse response = customerService.getAllAddressResponse(currentUser);
		return Response.of(response);
	}

	@DeleteMapping("/v1/customers/me/addresses/{addressId}")
	public Response<GetAllAddressResponse> deleteAddress(
		@PathVariable Long addressId,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		GetAllAddressResponse response = customerService.deleteAddress(addressId, currentUser);
		return Response.of(response);
	}
}
