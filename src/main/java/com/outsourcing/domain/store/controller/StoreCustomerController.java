package com.outsourcing.domain.store.controller;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.store.dto.response.StoreCustomerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.service.StoreCustomerService;
import com.outsourcing.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreCustomerController {

    private final StoreCustomerService storeCustomerService;

    // 공통 로직
    private Long getCustomerId(@AuthenticationPrincipal CustomUserDetails currentOwner) {
        if (!currentOwner.getUserInfo().getRole().equals(UserRole.CUSTOMER)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
        return currentOwner.getUserInfo().getId();
    }

    // Customer 입장에서의 가게 전체 조회(페이지네이션)
    @GetMapping("v1/customers/stores")
    public Response<Page<StoreCustomerResponse>> getAllPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StoreCustomerResponse> result = storeCustomerService.getAllPage(page,size);
        return Response.of(result);
    }

    // Customer 입장에서의 가게 단건 조회
    @GetMapping("/v1/customers/{customerId}/stores/{storeId}")
    public Response<StoreResponse> getOne(
            @AuthenticationPrincipal CustomUserDetails currentCustomer,
            @PathVariable Long storeId) {
        Long customerId = getCustomerId(currentCustomer);
        return Response.of(storeCustomerService.getStore(customerId,storeId));
    }
}
