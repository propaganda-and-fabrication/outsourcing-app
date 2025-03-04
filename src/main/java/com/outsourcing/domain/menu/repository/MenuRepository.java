package com.outsourcing.domain.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.outsourcing.domain.menu.entity.Menu;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByStoreId(Long storeId);
}