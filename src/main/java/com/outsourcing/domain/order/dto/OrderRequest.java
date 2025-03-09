package com.outsourcing.domain.order.dto;

import com.outsourcing.domain.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long storeId;
    private List<OrderItemResponse> menus;
}
