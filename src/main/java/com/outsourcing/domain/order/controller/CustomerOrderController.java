package com.outsourcing.domain.order.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.order.dto.OrderRequest;
import com.outsourcing.domain.order.dto.OrderResponse;
import com.outsourcing.domain.order.service.UserOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final UserOrderService userOrderService;

    // 주문 전체내역 확인
    @GetMapping("/v1/customers/orders")
    public Page<OrderResponse> getUserOrders(@AuthenticationPrincipal CustomUserDetails user, Pageable pageable) {
        return userOrderService.getUserOrders(user.getUserInfo().getId(), pageable);
    }


    @PostMapping("/v1/customers/orders")
    public Response<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest,
                                               @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = userOrderService.createOrder(user.getUserInfo().getId(),
                orderRequest.getStoreId(), orderRequest.getMenus());

        return Response.of(response);
    }
}
