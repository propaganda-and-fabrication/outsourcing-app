package com.outsourcing.domain.menu.dto.response;

import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.enums.MenuStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MenuResponse {

	private final Long id;
	private final Long storeId;
	private final String name;
	private final int price;
	private final String description;
	private String imageUrl;
	private final MenuStatus status;

	public static MenuResponse of(Menu menu) {
		return new MenuResponse(
			menu.getId(),
			menu.getStore().getId(),
			menu.getName(),
			menu.getPrice(),
			menu.getDescription(),
			menu.getImageUrl(),
			menu.getStatus()
		);
	}
}
