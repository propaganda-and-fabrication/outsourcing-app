package com.outsourcing.domain.store.dto.response;

import com.outsourcing.domain.store.entity.Store;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class StoreResponse {

    private final Long id;
    private final String storeName;
    private final String storeProfileUrl;
    private final String storeAddress;
    private final String storePhoneNumber;
    private final LocalTime openedAt;
    private final LocalTime closedAt;
    private final BigDecimal minPrice;
    private final List<Menu> menus;

    public static StoreResponse of(Store store, List<Menu> menus) {
        return new StoreResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreProfileUrl(),
                store.getStoreAddress(),
                store.getStorePhoneNumber(),
                store.getOpenedAt(),
                store.getClosedAt(),
                store.getMinPrice(),
                menus
        );
    }
}
