package com.outsourcing.domain.order.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.order.dto.OrderResponse;
import com.outsourcing.domain.order.service.OwnerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OwnerOrderController {

    private final OwnerOrderService ownerOrderService;

    // 지금 까지 받은 가게 주문 현황
    @GetMapping("/v1/owners/stores/{storeId}/orders")
    public Page<OrderResponse> getStoreOrders(@AuthenticationPrincipal CustomUserDetails owner, @PathVariable Long storeId, Pageable pageable) {
        return ownerOrderService.getStoreOrders(owner.getUserInfo().getId(), storeId, pageable);
    }


    @PatchMapping("/v1/owners/stores/{storeId}/orders/{orderId}")
    public Response<OrderResponse> cookingOrder(@AuthenticationPrincipal CustomUserDetails owner,
                                                @PathVariable Long storeId,
                                                @PathVariable Long orderId) {

        OrderResponse response = ownerOrderService.cookingOrder(owner.getUserInfo().getId()
                , storeId, orderId);
        return Response.of(response);
    }

    @PatchMapping("/v1/delivery/start")
    public Response<OrderResponse> deliveryStartOrder(@AuthenticationPrincipal CustomUserDetails owner) {

        OrderResponse response = ownerOrderService.startDelivery(owner.getUserInfo().getId());
        return Response.of(response);
    }

    @PatchMapping("/v1/delivery/complete")
    public Response<OrderResponse> deliveryCompleteOrder(@AuthenticationPrincipal CustomUserDetails owner) {

        OrderResponse response = ownerOrderService.completeDelivery(owner.getUserInfo().getId());
        return Response.of(response);
    }
}