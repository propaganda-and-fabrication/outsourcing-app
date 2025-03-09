package com.outsourcing.domain.menu.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.menu.enums.MenuStatus;
import com.outsourcing.domain.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "menus")
public class Menu extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
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

	public static Menu of(Store store, String name, int price, String description, String imageUrl, MenuStatus status) {
		return new Menu(store, name, price, description, imageUrl, status);
	}

	public void updateMenuDetails(String name, Integer price, String description) {
		this.name = name != null ? name : this.name;
		this.price = price != null ? price : this.price;
		this.description = description != null ? description : this.description;
	}

	public void updateStatus(MenuStatus status) {
		this.status = status;
	}

	public void updateImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public void deleteMenu() {
		this.deletedAt = LocalDateTime.now();
		this.status = MenuStatus.DELETED;
	}
}
