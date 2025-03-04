package com.outsourcing.domain.store.dto.response;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.outsourcing.domain.store.entity.Store;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StoreCustomerResponse {

	private final Long id;
	private final String storeName;
	private final String storeProfileUrl;
	private final String storeAddress;
	private final String storePhoneNumber;
	private final LocalTime openedAt;
	private final LocalTime closedAt;
	private final BigDecimal minPrice;

	public static StoreCustomerResponse of(Store store) {
		return new StoreCustomerResponse(
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
