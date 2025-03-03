package com.outsourcing.domain.store.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.store.enums.StoreStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Table(name = "stores")
public class Store extends BaseTime {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String storeName;

	private String storeProfileUrl;

	@Column(nullable = false, unique = true)
	private String storeAddress;

	@Column(nullable = false, unique = true)
	private String storePhoneNumber;

	@Column(nullable = false)
	private LocalTime openedAt;

	@Column(nullable = false)
	private LocalTime closedAt;

	@Enumerated(STRING)
	@Column(nullable = false)
	private StoreStatus storeStatus;

	@Column(nullable = false)
	private BigDecimal minPrice;

	public Store(String storeName, String storeProfileUrl, BigDecimal minPrice) {
		this.storeName = storeName;
		this.storeProfileUrl = storeProfileUrl;
		this.minPrice = minPrice;
	}
}
