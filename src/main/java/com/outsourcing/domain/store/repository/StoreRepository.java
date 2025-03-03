package com.outsourcing.domain.store.repository;

import com.outsourcing.domain.store.entitiy.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
