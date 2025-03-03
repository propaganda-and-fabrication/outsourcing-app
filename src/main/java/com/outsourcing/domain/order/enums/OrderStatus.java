package com.outsourcing.domain.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    ORDER_RECEIVED,
    COOKING,
    DELIVERING,
    DELIVERY_COMPLETED
}
