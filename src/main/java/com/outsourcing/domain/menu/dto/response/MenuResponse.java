package com.outsourcing.domain.menu.dto.response;

import com.outsourcing.domain.menu.enums.MenuStatus;

import lombok.Getter;

@Getter
public class MenuResponse {

	private final Long id;
	private final Long storeId;
	private final String name;
	private final int price;
	private final String description;
	private String imageUrl;
	private final MenuStatus status;

	public MenuResponse(Long id, Long storeId, String name, int price, String description, String imageUrl,
		MenuStatus status) {
		this.id = id;
		this.storeId = storeId;
		this.name = name;
		this.price = price;
		this.description = description;
		this.imageUrl = imageUrl;
		this.status = status;
	}
}
