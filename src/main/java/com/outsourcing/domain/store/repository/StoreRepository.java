package com.outsourcing.domain.store.repository;

import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.enums.StoreStatus;
import com.outsourcing.domain.user.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    int countStoreByOwner(Owner owner);

    List<Store> findByStoreStatus(StoreStatus storeStatus);
}
