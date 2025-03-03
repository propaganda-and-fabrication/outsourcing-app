package com.outsourcing.domain.menu.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.dto.request.MenuRequest;
import com.outsourcing.domain.menu.dto.response.MenuResponse;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.store.StoreRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;

	// 메뉴 생성
	@Transactional
	public MenuResponse createMenu(@Valid MenuRequest request, String ownerEmail) {

		// 가게 존재 여부 확인
		Store store = storeRepository.findById(request.getStoreId())
			.orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));

		// 현재 로그인한 사장님이 해당 가게의 주인인지 검증
		if (!store.getOwner().getEmail().equals(ownerEmail)) {
			throw new BaseException(ErrorCode.INVALID_STORE_ACCESS);
		}

		Menu menu = Menu.of(store, request);
		Menu savedMenu = menuRepository.save(menu);
		return MenuResponse.of(savedMenu);
	}
}
