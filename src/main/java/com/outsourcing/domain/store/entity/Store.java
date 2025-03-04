package com.outsourcing.domain.store.entity;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.store.enums.StoreStatus;
import com.outsourcing.domain.user.entity.Owner;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

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

    public Store (
            String storeName,
            String storeProfileUrl,
            String storeAddress,
            String storePhoneNumber,
            LocalTime openedAt,
            LocalTime closedAt,
            BigDecimal minPrice,
            StoreStatus storeStatus
            ) {
        this.storeName = storeName;
        this.storeProfileUrl = storeProfileUrl;
        this.storeAddress = storeAddress;
        this.storePhoneNumber = storePhoneNumber;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.minPrice = minPrice;
        this.storeStatus = storeStatus;
    }

    public void setOwner(Owner owner) {
    }

    public void updateStoreName(String storeName) {this.storeName = storeName;}

    public void updateProfileUrl(String storeProfileUrl) {this.storeProfileUrl = storeProfileUrl;}

    public void updateStoreAddress(String storeAddress) {this.storeAddress = storeAddress;}

    public void updateStorePhoneNumber(String storePhoneNumber) {this.storePhoneNumber = storePhoneNumber;}

    public void updateStoreHours(LocalTime openedAt, LocalTime closedAt) {this.openedAt = openedAt; this.closedAt = closedAt;}

    public void updateStoreStatus(StoreStatus storeStatus) {this.storeStatus = storeStatus;}

    public void updateMinPrice(BigDecimal minPrice) {this.minPrice = minPrice;}
}
