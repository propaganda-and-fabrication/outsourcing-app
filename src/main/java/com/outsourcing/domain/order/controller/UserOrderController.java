package com.outsourcing.domain.order.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.order.dto.OrderRequest;
import com.outsourcing.domain.order.dto.OrderResponse;
import com.outsourcing.domain.order.service.UserOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserOrderController {

    private final UserOrderService userOrderService;

    @PostMapping("/v1/customers/order")
    public Response<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest,
                                                     @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = userOrderService.createOrder(user.getUserInfo().getId(),
                orderRequest.getStoreId(), orderRequest.getMenus());

        return Response.of(response);
    }

    // 주문 전체내역 확인

    @PatchMapping("/v1/orders/{orderId}")
    public Response<OrderResponse> cancelOrder(@PathVariable Long orderId,
                                               @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = userOrderService.withdrawOrder(user.getUserInfo().getId(), orderId);

        return Response.of(response);
    }
}
