package com.outsourcing.domain.order.dto;

import com.outsourcing.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long storeId;
    private Long userId;
    private String deliveryAddress;
    private List<OrderItem> orderItems;
}
