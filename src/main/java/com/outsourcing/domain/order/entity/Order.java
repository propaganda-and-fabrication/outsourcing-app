package com.outsourcing.domain.order.entity;

import java.util.ArrayList;
import java.util.List;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.order.enums.OrderStatus;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@Column(nullable = false)
	private String deliveryAddress;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();

	@Column(nullable = false)
	private int totalPrice;

	// 주문 생성자 추가
	public Order(User user, Store store, String deliveryAddress, OrderStatus status) {
		this.user = user;
		this.store = store;
		this.deliveryAddress = deliveryAddress;
		this.status = status;
	}

	// 주문 항목 추가 메서드
	public void addOrderItem(OrderItem orderItem) {
		this.orderItems.add(orderItem);
	}

	// 주문 총 가격 계산 메서드
	public void calculateTotalPrice() {
		this.totalPrice = orderItems.stream()
				.mapToInt(OrderItem::getTotalPrice)
				.sum();
	}

	// 주문 상태 업데이트 메서드
	public void updateStatus(OrderStatus newStatus) {
		this.status = newStatus;
	}

}
