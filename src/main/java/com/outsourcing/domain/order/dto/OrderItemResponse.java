package com.outsourcing.domain.order.dto;

import com.outsourcing.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemResponse {
    private Long menuId;
    private String menuName;
    private int price;
    private int quantity;

    public OrderItemResponse(OrderItem orderItem) {
        this.menuId = orderItem.getMenu().getId();
        this.menuName = orderItem.getMenu().getName();
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getMenu().getPrice();
    }
}
