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
    private Long userId;
    private Long storeId;
    private List<OrderItem> menuItems;

}
