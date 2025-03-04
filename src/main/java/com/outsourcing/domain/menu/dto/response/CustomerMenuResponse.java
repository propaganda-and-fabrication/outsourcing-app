package com.outsourcing.domain.menu.dto.response;

import com.outsourcing.domain.menu.entity.Menu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class CustomerMenuResponse {

	private final Long id;
	private final Long storeId;
	private final String storeName;
	private final String name;
	private final int price;
	private final String description;
	private String imageUrl;

	public static CustomerMenuResponse of(Menu menu) {
		// if (menu.getStatus() != MenuStatus.AVAILABLE) {
		// 	return null; // Customer는 판매 중인 상품만 조회 가능
		// } -> 조회 기능 구현할때 service로 옮길 예정
		return new CustomerMenuResponse(
			menu.getId(),
			menu.getStore().getId(),
			menu.getStore().getStoreName(),
			menu.getName(),
			menu.getPrice(),
			menu.getDescription(),
			menu.getImageUrl()
		);
	}
}
