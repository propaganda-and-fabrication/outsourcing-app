package com.outsourcing.domain.store.dto.response;

import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.enums.StoreStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class StoreOwnerResponse {

    private final Long id;
    private final String storeName;
    private final String storeProfileUrl;
    private final String storeAddress;
    private final String storePhoneNumber;
    private final LocalTime openedAt;
    private final LocalTime closedAt;
    private final StoreStatus storeStatus;
    private final BigDecimal minPrice;

    public static StoreOwnerResponse of(Store store) {
        return new StoreOwnerResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreProfileUrl(),
                store.getStoreAddress(),
                store.getStorePhoneNumber(),
                store.getOpenedAt(),
                store.getClosedAt(),
                store.getStoreStatus(),
                store.getMinPrice()
        );
    }
}
