package com.outsourcing.domain.menu.dto.response;

import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.enums.MenuStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerMenuResponse {

	private final Long id;
	private final Long storeId;
	private final String storeName;
	private final String name;
	private final int price;
	private final String description;
	private String imageUrl;
	private final MenuStatus status;

	public static OwnerMenuResponse of(Menu menu) {
		// if (menu.getStatus() == MenuStatus.HIDDEN) {
		// 	return null; // Owner가 숨긴 메뉴는 보이지 않도록 처리
		// } -> 조회 기능 구현할때 service로 옮길 예정
		return new OwnerMenuResponse(
			menu.getId(),
			menu.getStore().getId(),
			menu.getStore().getStoreName(),
			menu.getName(),
			menu.getPrice(),
			menu.getDescription(),
			menu.getImageUrl(),
			menu.getStatus()
		);
	}
}
