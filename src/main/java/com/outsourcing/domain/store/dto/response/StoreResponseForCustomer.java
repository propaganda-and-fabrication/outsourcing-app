package com.outsourcing.domain.store.dto.response;

import com.outsourcing.domain.store.entitiy.Store;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class StoreResponseForCustomer {

    private final Long id;
    private final String storeName;
    private final String storeProfileUrl;
    private final String storeAddress;
    private final String storePhoneNumber;
    private final LocalTime openedAt;
    private final LocalTime closedAt;
    private final BigDecimal minPrice;

    public static StoreResponseForCustomer of(Store store) {
        return new StoreResponseForCustomer(
                store.getId(),
                store.getStoreName(),
                store.getStoreProfileUrl(),
                store.getStoreAddress(),
                store.getStorePhoneNumber(),
                store.getOpenedAt(),
                store.getClosedAt(),
                store.getMinPrice()
        );
    }
}
