package com.outsourcing.domain.menu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.menu.dto.request.MenuRequest;
import com.outsourcing.domain.menu.dto.response.MenuResponse;
import com.outsourcing.domain.menu.service.MenuService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MenuController {

	private final MenuService menuService;

	// 메뉴 생성
	@PostMapping("v1/menus")
	public ResponseEntity<MenuResponse> createMenu(
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		@Valid @RequestBody MenuRequest request) {
		String ownerEmail = customUserDetails.getUsername();
		MenuResponse menuResponse = menuService.createMenu(request, ownerEmail);
		return new ResponseEntity<>(menuResponse, HttpStatus.CREATED);
	}
}
