package com.outsourcing.domain.menu.entity;

import java.time.LocalDateTime;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.menu.dto.request.MenuRequest;
import com.outsourcing.domain.menu.enums.MenuStatus;
import com.outsourcing.domain.store.entity.Store;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Menu extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	private String name;

	private int price;

	private String description;

	private String imageUrl;

	@Enumerated(EnumType.STRING)
	private MenuStatus status;

	private LocalDateTime deletedAt;

	public Menu(Store store, String name, int price, String description, String imageUrl, MenuStatus status) {
		this.store = store;
		this.name = name;
		this.price = price;
		this.description = description;
		this.imageUrl = imageUrl;
		this.status = status;
	}

	public static Menu of(Store store, MenuRequest request) {
		return new Menu(
			store,
			request.getName(),
			request.getPrice(),
			request.getDescription(),
			request.getImageUrl(),
			request.getStatus()
		);
	}

	public static Menu update(Menu menu, String name, int price, String description, String imageUrl,
		MenuStatus status) {
		menu.price = price;
		menu.description = description;
		menu.imageUrl = imageUrl;
		menu.status = status;
		return menu;
	}

	public void deleteMenu() {
		this.deletedAt = LocalDateTime.now();
	}
}
