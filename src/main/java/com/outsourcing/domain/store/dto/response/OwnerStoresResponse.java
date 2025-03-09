package com.outsourcing.domain.store.dto.response;

import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.enums.StoreStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public class OwnerStoresResponse {

    private final Long id;
    private final String storeName;
    private final String storeProfileUrl;
    private final String storeAddress;
    private final BigDecimal minPrice;
    private final StoreStatus storeStatus;

    public static OwnerStoresResponse of(Store store) {
        return new OwnerStoresResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreProfileUrl(),
                store.getStoreAddress(),
                store.getMinPrice(),
                store.getStoreStatus()
        );
    }
}
