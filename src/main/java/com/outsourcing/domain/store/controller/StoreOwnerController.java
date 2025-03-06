package com.outsourcing.domain.store.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.store.dto.request.*;
import com.outsourcing.domain.store.dto.response.OwnerStoresResponse;
import com.outsourcing.domain.store.dto.response.StoreOwnerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.service.StoreOwnerService;
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

    // 가게 생성
    @PostMapping("/v1/owners/stores")
    public Response<StoreOwnerResponse> createStore (
            @AuthenticationPrincipal CustomUserDetails currentOwner,
            @Valid @RequestBody CreateStoreRequest request
    ) {
        StoreOwnerResponse response = storeOwnerService.createStore(currentOwner.getUserInfo().getId(), request);
        return Response.of(response);
    }

    // 내 가게 전체 조회
    @GetMapping("/v1/owners/stores")
    public Response<List<OwnerStoresResponse>> getAll(
            @AuthenticationPrincipal CustomUserDetails currentOwner) {
        return Response.of(storeOwnerService.getAll(currentOwner.getUserInfo().getId()));
    }

    // 내 가게 단건 조회
    @GetMapping("/v1/owners/stores/{storeId}")
    public Response<StoreResponse> getOne(
            @AuthenticationPrincipal CustomUserDetails currentOwner,
            @PathVariable Long storeId) {
        return Response.of(storeOwnerService.getStore(currentOwner.getUserInfo().getId(), storeId));
    }

    // 가게 이름 수정
    @PatchMapping("/v1/owners/stores/{storeId}/name")
    public Response<StoreOwnerResponse> updateStoreName(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateNameRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        StoreOwnerResponse response = storeOwnerService.updateStoreName(storeId,request,currentOwner.getUserInfo().getId());
        return Response.of(response);
    }

    // 가게 이미지 수정
    @PatchMapping("/v1/owners/stores/{storeId}/image")
    public Response<StoreOwnerResponse> updateProfileUrl(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateImageRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        StoreOwnerResponse response = storeOwnerService.updateProfileUrl(storeId,request,currentOwner.getUserInfo().getId());
        return Response.of(response);
    }

    //가게 주소 수정
    @PatchMapping("/v1/owners/stores/{storeId}/address")
    public Response<StoreOwnerResponse> updateStoreAddress(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateAddressRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        StoreOwnerResponse response = storeOwnerService.updateStoreAddress(storeId,request,currentOwner.getUserInfo().getId());
        return Response.of(response);
    }

    // 가게 전화번호 수정
    @PatchMapping("/v1/owners/stores/{storeId}/phone")
    public Response<StoreOwnerResponse> updateStorePhoneNumber(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateNumberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        StoreOwnerResponse response = storeOwnerService.updateStorePhoneNumber(storeId,request,currentOwner.getUserInfo().getId());
        return Response.of(response);
    }

    // 가게 영업시간 수정
    @PatchMapping("/v1/owners/stores/{storeId}/hours")
    public Response<StoreOwnerResponse> updateStoreHours(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreHoursRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        StoreOwnerResponse response = storeOwnerService.updateStoreHours(storeId,request,currentOwner.getUserInfo().getId());
        return Response.of(response);
    }

    // 가게 상태 수정
    @PatchMapping("/v1/owners/stores/{storeId}/status")
    public Response<StoreOwnerResponse> updateStoreStatus(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        StoreOwnerResponse response = storeOwnerService.updateStoreStatus(storeId,request,currentOwner.getUserInfo().getId());
        return Response.of(response);
    }

    // 가게 최소금액 수정
    @PatchMapping("/v1/owners/stores/{storeId}/min-price")
    public Response<StoreOwnerResponse> updateMinPrice(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateMinPriceRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        StoreOwnerResponse response = storeOwnerService.updateMinPrice(storeId,request,currentOwner.getUserInfo().getId());
        return Response.of(response);
    }
    //가게 삭제(실질적인 삭제가 아닌 영업 상태만 변경)
    @PatchMapping("/v1/owners/stores/{storeId}/shutdown")
    public void deleteStore(@PathVariable Long storeId, @AuthenticationPrincipal CustomUserDetails currentOwner) {
        storeOwnerService.deleteById(storeId, currentOwner.getUserInfo().getId());
    }
}
