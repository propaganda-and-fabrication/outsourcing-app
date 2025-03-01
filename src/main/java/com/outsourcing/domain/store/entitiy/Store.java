package com.outsourcing.domain.store.entitiy;

import com.outsourcing.common.entity.BaseTime;
import com.outsourcing.domain.store.enums.StoreStatus;
import com.outsourcing.domain.user.entity.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;

@Getter
@Entity
@NoArgsConstructor
public class Store extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String storeName;

    private String storeProfileUrl;

    @OneToOne(mappedBy = "store", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private Address address;

    @Column(nullable = false, unique = true)
    private String storeNumber;

    @Column(nullable = false)
    private LocalDateTime openedAt;

    @Column(nullable = false)
    private LocalDateTime closedAt;

    @Enumerated(STRING)
    @Column(nullable = false)
    private StoreStatus storeStatus;

    @Column(nullable = false)
    private BigDecimal minPrice;

    public Store(
            String storeName,
            String storeProfileUrl,
            Address address,
            String storeNumber,
            LocalDateTime openedAt,
            LocalDateTime closedAt,
            StoreStatus storeStatus,
            BigDecimal minPrice
    ) {
        this.storeName = storeName;
        this.storeProfileUrl = storeProfileUrl;
        this.address = address;
        this.storeNumber = storeNumber;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.storeStatus = storeStatus;
        this.minPrice = minPrice;
    }
}
