package com.outsourcing.domain.order.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.order.dto.OrderResponse;
import com.outsourcing.domain.order.service.OwnerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OwnerOrderController {

    private final OwnerOrderService ownerOrderService;

    @PatchMapping("/v1/{storeId}/orders/{orderId}")
    public Response<OrderResponse> cookingOrder(@AuthenticationPrincipal CustomUserDetails owner,
                                                @PathVariable Long storeId,
                                                @PathVariable Long orderId) {

        OrderResponse response = ownerOrderService.cookingOrder(owner.getUserInfo().getId()
                , storeId, orderId);
        return Response.of(response);
    }

    @PatchMapping("/v1/start/delivery")
    public Response<OrderResponse> deliveryStartOrder(@AuthenticationPrincipal CustomUserDetails owner) {

        OrderResponse response = ownerOrderService.startDelivery(owner.getUserInfo().getId());
        return Response.of(response);
    }

    @PatchMapping("/v1/complete/delivery")
    public Response<OrderResponse> deliveryCompleteOrder(@AuthenticationPrincipal CustomUserDetails owner) {

        OrderResponse response = ownerOrderService.completeDelivery(owner.getUserInfo().getId());
        return Response.of(response);
    }
}