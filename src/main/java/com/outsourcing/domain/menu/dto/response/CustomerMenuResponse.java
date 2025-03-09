package com.outsourcing.domain.menu.dto.response;

import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.enums.MenuStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerMenuResponse {

	private final Long id;
	private final Long storeId;
	private final String storeName;
	private final String name;
	private final int price;
	private final String description;
	private String imageUrl;
	private MenuStatus status;

	public static CustomerMenuResponse of(Menu menu) {
		return new CustomerMenuResponse(
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
