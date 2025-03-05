package com.outsourcing.domain.store.controller;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.store.dto.request.*;
import com.outsourcing.domain.store.dto.response.StoreOwnerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.service.StoreOwnerService;
import com.outsourcing.domain.user.dto.request.customer.UpdateProfileUrlRequest;
import com.outsourcing.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreOwnerController {

    private final StoreOwnerService storeOwnerService;

    // 공통 구현 메서드
    private Long getOwnerId(@AuthenticationPrincipal CustomUserDetails currentOwner) {
        if (!currentOwner.getUserInfo().getRole().equals(UserRole.OWNER)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }
        return currentOwner.getUserInfo().getId();
    }

    // 가게 생성
    @PostMapping("/v1/owners/stores")
    public Response<StoreOwnerResponse> createStore (
            @AuthenticationPrincipal CustomUserDetails currentOwner,
            @Valid @RequestBody CreateStoreRequest request
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.createStore(ownerId,request);
        return Response.of(response);
    }

    // 내 가게 전체 조회
    @GetMapping("/v1/owners/{ownerId}/stores")
    public Response<List<StoreOwnerResponse>> getAll(
            @AuthenticationPrincipal CustomUserDetails currentOwner) {
        Long ownerId = getOwnerId(currentOwner);
        return Response.of(storeOwnerService.getAll(ownerId));
    }

    // 내 가게 단건 조회
    @GetMapping("/v1/owners/{ownerId}/stores/{storeId}")
    public Response<StoreResponse> getOne(
            @AuthenticationPrincipal CustomUserDetails currentOwner,
            @PathVariable Long storeId) {
        Long ownerId = getOwnerId(currentOwner);
        return Response.of(storeOwnerService.getStore(ownerId,storeId));
    }

    // 가게 이름 수정
    @PatchMapping("/v1/owners/stores/{storeId}/name")
    public Response<StoreOwnerResponse> updateStoreName(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateNameRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.updateStoreName(storeId,request,ownerId);
        return Response.of(response);
    }

    // 가게 이미지 수정
    @PatchMapping("/v1/owners/stores/{storeId}/profile-image")
    public Response<StoreOwnerResponse> updateProfileUrl(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateProfileUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.updateProfileUrl(storeId,request,ownerId);
        return Response.of(response);
    }

    //가게 주소 수정
    @PatchMapping("/v1/owners/stores/{storeId}/store-address")
    public Response<StoreOwnerResponse> updateStoreAddress(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateAddressRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.updateStoreAddress(storeId,request,ownerId);
        return Response.of(response);
    }

    // 가게 전화번호 수정
    @PatchMapping("/v1/owners/stores/{storeId}/store-phone")
    public Response<StoreOwnerResponse> updateStorePhoneNumber(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateNumberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.updateStorePhoneNumber(storeId,request,ownerId);
        return Response.of(response);
    }

    // 가게 영업시간 수정
    @PatchMapping("/v1/owners/stores/{storeId}/store-hours")
    public Response<StoreOwnerResponse> updateStoreHours(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreHoursRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.updateStoreHours(storeId,request,ownerId);
        return Response.of(response);
    }

    // 가게 상태 수정
    @PatchMapping("/v1/owners/stores/{storeId}/store-status")
    public Response<StoreOwnerResponse> updateStoreStatus(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.updateStoreStatus(storeId,request,ownerId);
        return Response.of(response);
    }

    // 가게 최소금액 수정
    @PatchMapping("/v1/owners/stores/{storeId}/min-price")
    public Response<StoreOwnerResponse> updateMinPrice(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateMinPriceRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        Long ownerId = getOwnerId(currentOwner);
        StoreOwnerResponse response = storeOwnerService.updateMinPrice(storeId,request,ownerId);
        return Response.of(response);
    }
    //가게 삭제(실질적인 삭제가 아닌 영업 상태만 변경)
    @PatchMapping("/v1/owners/stores/{storeId}")
    public void deleteStore(@PathVariable Long storeId, @AuthenticationPrincipal CustomUserDetails currentOwner) {
        Long ownerId = getOwnerId(currentOwner);
        storeOwnerService.deleteById(storeId, ownerId);
    }
}
