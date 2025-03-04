package com.outsourcing.domain.store.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.auth.service.CustomUserDetails;
import com.outsourcing.domain.store.dto.request.*;
import com.outsourcing.domain.store.dto.response.StoreCustomerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.dto.response.StoreOwnerResponse;
import com.outsourcing.domain.store.enums.StoreStatus;
import com.outsourcing.domain.store.service.StoreService;
import com.outsourcing.domain.user.dto.request.customer.UpdateProfileUrlRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreController {

    private final StoreService storeService;

    // 공통 구현 메서드
    private String getOwnerEmail(@AuthenticationPrincipal CustomUserDetails currentOwner) {
        return currentOwner.getUserInfo().getEmail();
    }

    // 가게 생성
    @PostMapping("/v1/owners/stores")
    public Response<StoreOwnerResponse> createStore (
            @AuthenticationPrincipal CustomUserDetails currentOwner,
            @Valid @RequestBody CreateStoreRequest request
    ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.createStore(ownerEmail,request);
        return Response.of(response, "가게 등록 성공");
    }

    // Customer 입장에서의 가게 전체 조회
    @GetMapping("/v1/customers/stores")
    public Response<List<StoreCustomerResponse>> getAll() {
        return Response.of(storeService.findAll(), "가게 전체 조회 성공");
    }

    // Owner 입장에서의 가게 전체 조회
    @GetMapping("/v1/customers/stores")
    public Response<List<StoreOwnerResponse>> getAll(StoreStatus storeStatus) {
        return Response.of(storeService.findAll(storeStatus), "가게 전체 조회 성공");
    }

//    @GetMapping("/v1/owners/stores/{storeId}")
//    public Response<StoreResponse> getOne(@PathVariable Long storeId) {
//        return Response.of(storeService.findById(storeId), "가게 단건 조회 성공");
//    }

    // 가게 이름 수정
    @PatchMapping("/v1/owners/stores/{storeId}/update/name")
    public Response<StoreOwnerResponse> updateStoreName(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateNameRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
            ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.updateStoreName(storeId,request,ownerEmail);
        return Response.of(response, "가게 이름 수정 성공");
    }

    // 가게 이미지 수정
    @PatchMapping("/v1/owners/stores/{storeId}/update/profileUrl")
    public Response<StoreOwnerResponse> updateProfileUrl(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateProfileUrlRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.updateProfileUrl(storeId,request,ownerEmail);
        return Response.of(response, "가게 이미지 수정 성공");
    }

    //가게 주소 수정
    @PatchMapping("/v1/owners/stores/{storeId}/update/storeAddress")
    public Response<StoreOwnerResponse> updateStoreAddress(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateAddressRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.updateStoreAddress(storeId,request,ownerEmail);
        return Response.of(response, "가게 주소 수정 성공");
    }

    // 가게 전화번호 수정
    @PatchMapping("/v1/owners/stores/{storeId}/update/storePhoneNumber")
    public Response<StoreOwnerResponse> updateStorePhoneNumber(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateNumberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.updateStorePhoneNumber(storeId,request,ownerEmail);
        return Response.of(response, "가게 전화번호 수정 성공");
    }

    // 가게 영업시간 수정
    @PatchMapping("/v1/owners/stores/{storeId}/update/storeHours")
    public Response<StoreOwnerResponse> updateStoreHours(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStoreHoursRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.updateStoreHours(storeId,request,ownerEmail);
        return Response.of(response, "가게 운영시간 수정 성공");
    }

    // 가게 상태 수정
    @PatchMapping("/v1/owners/stores/{storeId}/update/storeStatus")
    public Response<StoreOwnerResponse> updateStoreStatus(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.updateStoreStatus(storeId,request,ownerEmail);
        return Response.of(response, "가게 운영상태 수정 성공");
    }

    // 가게 최소금액 수정
    @PatchMapping("/v1/owners/stores/{storeId}/update/minPrice")
    public Response<StoreOwnerResponse> updateMinPrice(
            @PathVariable Long storeId,
            @Valid @RequestBody UpdateMinPriceRequest request,
            @AuthenticationPrincipal CustomUserDetails currentOwner
    ) {
        String ownerEmail = getOwnerEmail(currentOwner);
        StoreOwnerResponse response = storeService.updateMinPrice(storeId,request,ownerEmail);
        return Response.of(response, "가게 최소금액 수정 성공");
    }
}