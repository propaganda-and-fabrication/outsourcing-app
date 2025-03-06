package com.outsourcing.domain.menu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.enums.MenuStatus;

import io.lettuce.core.dynamic.annotation.Param;

public interface MenuRepository extends JpaRepository<Menu, Long> {

	List<Menu> findAllByStoreId(Long storeId);

	Optional<Menu> findByStoreIdAndId(Long storeId, Long menuId);

	@Query("SELECT menu FROM Menu menu WHERE menu.store.id = :storeId AND menu.status IN (:statuses)")
	List<Menu> findAvailableMenusByStore(@Param("storeId") Long storeId, @Param("statuses") List<MenuStatus> statuses);

	@Query("SELECT menu FROM Menu menu WHERE menu.store.id = :storeId AND menu.status != :status")
	List<Menu> findNonDeletedMenusByStore(@Param("storeId") Long storeId, @Param("status") MenuStatus status);
}
