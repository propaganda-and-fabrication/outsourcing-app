package com.outsourcing.domain.store.entitiy;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.store.enums.StoreStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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
