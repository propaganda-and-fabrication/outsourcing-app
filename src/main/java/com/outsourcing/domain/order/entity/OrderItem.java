package com.outsourcing.domain.order.entity;

import com.outsourcing.domain.menu.entity.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "order_items")
public class OrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter(AccessLevel.NONE)
	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false)
	private int price;

	public OrderItem(Order order, Menu menu, int quantity) {
		this.order = order;
		this.menu = menu;
		this.quantity = quantity;
		this.price = menu.getPrice();
	}

	public int getTotalPrice() {
		return this.price * this.quantity;
	}

}
