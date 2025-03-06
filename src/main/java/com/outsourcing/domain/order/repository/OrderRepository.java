package com.outsourcing.domain.order.repository;

import com.outsourcing.domain.order.entity.Order;
import com.outsourcing.domain.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Order findByIdAndStatus(Long id, OrderStatus status);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStoreId(Long storeId, Pageable pageable);
}
