package com.outsourcing.domain.store.controller;

import com.outsourcing.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreController {

    private final StoreService storeService;
//    private final StoreRepository storeRepository;

//    @PostMapping("/v1/stores")
//    public Response<StoreOwnerResponser> createStore (
//            @AuthenticationPrincipal CustomUserDetails currentOwner,
//            @Valid @RequestBody CreateStoreRequest request
//    ) {
//        StoreOwnerResponse response = storeService.createStore(currentOwner,request);
//        return Response.of(response, "가게 등록 성공");
//    }
}