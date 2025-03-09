package com.outsourcing.domain.store.dto.response;

import com.outsourcing.domain.store.entity.Store;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;


@Getter
@RequiredArgsConstructor
public class StoreCustomerResponse {

    private final Long id;
    private final String storeName;
    private final String storeProfileUrl;
    private final BigDecimal minPrice;

    public static StoreCustomerResponse of(Store store) {
        return new StoreCustomerResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreProfileUrl(),
                store.getMinPrice()
        );
    }
}
