package com.outsourcing.domain.store.repository;

import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.enums.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Page<Store> findByStoreStatus(StoreStatus storeStatus, Pageable pageable);

    @Query("SELECT s FROM Store s WHERE s.owner.id = :ownerId")
    List<Store> findByOwnerId(@Param("ownerId") Long ownerId);
}