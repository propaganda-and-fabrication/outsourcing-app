package com.outsourcing.domain.order.service;

import com.outsourcing.domain.order.dto.OrderItemDto;
import com.outsourcing.domain.order.dto.OrderResponse;
import com.outsourcing.domain.order.repository.OrderRepository;

import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
//    private final MenuRepository menuRepository;

    public OrderResponse createOrder(Long userId, List<OrderItemDto> menus) {
        // 사용자 검색
        User user = customerRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        // 가게 정보

    }
}
