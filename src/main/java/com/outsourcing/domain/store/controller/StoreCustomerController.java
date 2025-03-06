package com.outsourcing.domain.store.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.store.dto.response.StoreCustomerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.service.StoreCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreCustomerController {

    private final StoreCustomerService storeCustomerService;

    // Customer 입장에서의 가게 전체 조회(페이지네이션)
    @GetMapping("/v1/customers/stores")
    public Response<Page<StoreCustomerResponse>> getAllPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StoreCustomerResponse> result = storeCustomerService.getAllPage(page,size);
        return Response.of(result);
    }

    // Customer 입장에서의 가게 단건 조회
    @GetMapping("/v1/customers/stores/{storeId}")
    public Response<StoreResponse> getOne(@PathVariable Long storeId) {
        return Response.of(storeCustomerService.getStore(storeId));
    }
}
