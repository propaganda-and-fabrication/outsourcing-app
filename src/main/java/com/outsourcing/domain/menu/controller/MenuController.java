package com.outsourcing.domain.menu.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.menu.dto.request.CreateMenuRequest;
import com.outsourcing.domain.menu.dto.request.UpdateMenuRequest;
import com.outsourcing.domain.menu.dto.response.CustomerMenuResponse;
import com.outsourcing.domain.menu.dto.response.OwnerMenuResponse;
import com.outsourcing.domain.menu.enums.MenuStatus;
import com.outsourcing.domain.menu.service.MenuService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

	private final MenuService menuService;

	private String getOwnerEmail(CustomUserDetails currentUser) {
		return currentUser.getUsername();
	}

	// 메뉴 생성
	@PostMapping("v1/owners/stores/{storeId}/menus")
	public Response<OwnerMenuResponse> createMenu(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid @RequestBody CreateMenuRequest request) {

		OwnerMenuResponse response = menuService.createMenu(request, getOwnerEmail(currentUser));
		return Response.of(response);
	}

	// 메뉴 조회 (고객)
	@GetMapping("v1/customers/stores/{storeId}/menus")
	public Response<List<CustomerMenuResponse>> getCustomerMenus(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long storeId
	) {

		String customerEmail = currentUser.getUsername();
		List<CustomerMenuResponse> response = menuService.getCustomerMenus(storeId, customerEmail);
		return Response.of(response);
	}

	// 메뉴 조회 (사장)
	@GetMapping("v1/owners/stores/{storeId}/menus")
	public Response<List<OwnerMenuResponse>> getOwnerMenus(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long storeId
	) {

		String ownerEmail = currentUser.getUsername();
		List<OwnerMenuResponse> response = menuService.getOwnerMenus(storeId, ownerEmail);
		return Response.of(response);
	}

	// 메뉴 수정 (이름, 가격, 내용)
	@PatchMapping("v1/owners/menus/{menuId}/details")
	public Response<OwnerMenuResponse> updateMenuDetails(
		@PathVariable Long menuId,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid @RequestBody UpdateMenuRequest request) {

		OwnerMenuResponse response = menuService.updateMenuDetails(menuId, request, getOwnerEmail(currentUser));
		return Response.of(response);
	}

	// 메뉴 수정 (상태)
	@PatchMapping("v1/owners/menus/{menuId}/status")
	public Response<OwnerMenuResponse> updateMenuStatus(
		@PathVariable Long menuId,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@RequestParam MenuStatus status) {

		OwnerMenuResponse response = menuService.updateMenuStatus(menuId, status, getOwnerEmail(currentUser));
		return Response.of(response);
	}

	// 메뉴 수정 (이미지)
	@PatchMapping("v1/owners/menus/{menuId}/imageUrl")
	public Response<OwnerMenuResponse> updateImageUrl(
		@PathVariable Long menuId,
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@RequestParam String imageUrl) {

		OwnerMenuResponse response = menuService.updateImageUrl(menuId, imageUrl, getOwnerEmail(currentUser));
		return Response.of(response);
	}

	// 메뉴 삭제 (soft delete)
	@DeleteMapping("v1/owners/menus/{menuId}")
	public Response<Void> deleteMenu(
		@PathVariable Long menuId,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {

		menuService.deleteMenu(menuId, getOwnerEmail(currentUser));
		return Response.of(null);
	}
}
