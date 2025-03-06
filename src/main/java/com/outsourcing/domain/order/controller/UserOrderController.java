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

    // 사장님 또는 손님 주문 취소
    @PatchMapping("/v1/orders/{orderId}")
    public Response<OrderResponse> cancelOrder(@PathVariable Long orderId,
                                               @AuthenticationPrincipal CustomUserDetails user) {
        OrderResponse response = userOrderService.withdrawOrder(user.getUserInfo().getId(), orderId);

        return Response.of(response);
    }
}
