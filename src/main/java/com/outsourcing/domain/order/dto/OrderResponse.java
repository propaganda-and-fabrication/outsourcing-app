package com.outsourcing.domain.order.dto;

import com.outsourcing.domain.order.entity.Order;
import com.outsourcing.domain.order.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String storeName;
    private String deliveryAddress;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private List<OrderItemResponse> orderItems;
    private int totalPrice;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.storeName = order.getStore().getStoreName();
        this.deliveryAddress = order.getDeliveryAddress();
        this.status = order.getStatus();
        this.orderTime = order.getCreatedAt();
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::new) // OrderItem을 OrderItemResponse로 변환
                .collect(Collectors.toList());
        this.totalPrice = order.getTotalPrice();
    }
}
