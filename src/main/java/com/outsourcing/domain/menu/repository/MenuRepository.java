package com.outsourcing.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.outsourcing.domain.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findAllByStoreId(Long storeId);
}
