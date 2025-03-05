package com.outsourcing.domain.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.dto.request.CreateMenuRequest;
import com.outsourcing.domain.menu.dto.request.UpdateMenuRequest;
import com.outsourcing.domain.menu.dto.response.CustomerMenuResponse;
import com.outsourcing.domain.menu.dto.response.OwnerMenuResponse;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.enums.MenuStatus;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.repository.StoreRepository;
import com.outsourcing.domain.user.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	private final CustomerRepository customerRepository;

	// 메뉴 생성
	@Transactional
	public OwnerMenuResponse createMenu(CreateMenuRequest request, String ownerEmail) {

		// 가게 존재 여부 및 소유자 검증
		Store store = validateStoreOwnership(request.getStoreId(), ownerEmail);

		// 상태 기본값은 AVAILABLE
		MenuStatus status = (request.getStatus() != null) ? request.getStatus() : MenuStatus.AVAILABLE;

		Menu menu = Menu.of(store,
			request.getName(),
			request.getPrice(),
			request.getDescription(),
			request.getImageUrl(),
			status
		);

		Menu savedMenu = menuRepository.save(menu);
		return OwnerMenuResponse.of(savedMenu);
	}

	// 메뉴 조회 (고객)
	@Transactional(readOnly = true)
	public List<CustomerMenuResponse> getCustomerMenus(Long storeId, String customerEmail) {

		// 고객 회원 및 가게 존재 여부 검증
		Store store = validateCustomerAndStore(storeId, customerEmail);

		List<Menu> menus = menuRepository.findByStoreId(storeId);

		return menus.stream()
			.filter(menu -> menu.getStatus() == MenuStatus.AVAILABLE || menu.getStatus() == MenuStatus.SOLD_OUT)
			.map(menu -> new CustomerMenuResponse(
				menu.getId(),
				menu.getStore().getId(),
				menu.getStore().getStoreName(),
				menu.getName(),
				menu.getPrice(),
				menu.getDescription(),
				menu.getImageUrl(),
				menu.getStatus()
			))
			.toList();
	}

	// 메뉴 조회 (사장)
	@Transactional(readOnly = true)
	public List<OwnerMenuResponse> getOwnerMenus(Long storeId, String ownerEmail) {

		// 가게 존재 여부 및 소유자 검증
		Store store = validateStoreOwnership(storeId, ownerEmail);

		List<Menu> menus = menuRepository.findByStoreId(storeId);

		return menus.stream()
			.filter(menu -> menu.getStatus() != MenuStatus.DELETED)
			.map(menu -> new OwnerMenuResponse(
				menu.getId(),
				menu.getStore().getId(),
				menu.getStore().getStoreName(),
				menu.getName(),
				menu.getPrice(),
				menu.getDescription(),
				menu.getImageUrl(),
				menu.getStatus()
			))
			.toList();
	}

	// 메뉴 수정 (이름, 가격, 내용)
	@Transactional
	public OwnerMenuResponse updateMenuDetails(Long menuId, UpdateMenuRequest request, String ownerEmail) {

		// 메뉴 존재 여부 및 소유자 검증
		Menu menu = validateMenuOwnership(menuId, ownerEmail);

		menu.updateMenuDetails(request.getName(), request.getPrice(), request.getDescription());
		return OwnerMenuResponse.of(menu);
	}

	// 메뉴 수정 (상태)
	@Transactional
	public OwnerMenuResponse updateMenuStatus(Long menuId, MenuStatus status, String ownerEmail) {

		// 메뉴 존재 여부 및 소유자 검증
		Menu menu = validateMenuOwnership(menuId, ownerEmail);

		menu.updateStatus(status);
		return OwnerMenuResponse.of(menu);
	}

	// 메뉴 수정 (이미지)
	@Transactional
	public OwnerMenuResponse updateImageUrl(Long menuId, String imageUrl, String ownerEmail) {

		// 메뉴 존재 여부 및 소유자 검증
		Menu menu = validateMenuOwnership(menuId, ownerEmail);

		menu.updateImageUrl(imageUrl);
		return OwnerMenuResponse.of(menu);
	}

	// 메뉴 삭제 (soft delete)
	@Transactional
	public void deleteMenu(Long menuId, String ownerEmail) {

		// 메뉴 존재 여부 및 소유자 검증
		Menu menu = validateMenuOwnership(menuId, ownerEmail);

		// 이미 삭제된 메뉴는 다시 삭제할 수 없음
		if (menu.getStatus() == MenuStatus.DELETED) {
			throw new BaseException(ErrorCode.MENU_ALREADY_DELETED);
		}

		// deletedAt 설정 + 상태를 DELETED로 변경
		menu.deleteMenu();
	}

	//공통 검증 로직

	// 가게 존재 여부 및 소유자 검증
	private Store validateStoreOwnership(Long storeId, String ownerEmail) {
		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));

		if (!store.getOwner().getEmail().equals(ownerEmail)) {
			throw new BaseException(ErrorCode.INVALID_STORE_ACCESS);
		}
		return store;
	}

	// 고객 회원 및 가게 존재 여부 검증
	private Store validateCustomerAndStore(Long storeId, String customerEmail) {
		if (!customerRepository.existsByEmail(customerEmail)) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_USER);
		}

		Store store = storeRepository.findById(storeId)
			.orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));
		return store;
	}

	// 메뉴 존재 여부 및 소유자 검증
	private Menu validateMenuOwnership(Long menuId, String ownerEmail) {
		Menu menu = menuRepository.findById(menuId)
			.orElseThrow(() -> new BaseException(ErrorCode.MENU_NOT_FOUND));

		if (!menu.getStore().getOwner().getEmail().equals(ownerEmail)) {
			throw new BaseException(ErrorCode.INVALID_STORE_ACCESS);
		}
		return menu;
	}
}
