package com.outsourcing.domain.order.service;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.order.dto.OrderItemResponse;
import com.outsourcing.domain.order.dto.OrderResponse;
import com.outsourcing.domain.order.entity.Order;
import com.outsourcing.domain.order.entity.OrderItem;
import com.outsourcing.domain.order.enums.OrderStatus;
import com.outsourcing.domain.order.repository.OrderRepository;

import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.repository.StoreRepository;
import com.outsourcing.domain.user.dto.AddressDto;
import com.outsourcing.domain.user.entity.User;
import com.outsourcing.domain.user.repository.AddressRepository;
import com.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public OrderResponse createOrder(Long userId, Long storeId, List<OrderItemResponse> menus) {
        // 1. 사용자 및 가게 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));

        // 2. 사용자 기본 배송지 조회
        AddressDto userAddress = addressRepository.findAddressByCustomer_IdAndStatus(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.ADDRESS_NOT_FOUND));

        // 3. 주문 생성
        Order order = new Order(user, store, userAddress.getAddress(), OrderStatus.ORDER_RECEIVED);

        // 4. 메뉴별 OrderItem 생성 후 Order 에 추가
        List<OrderItem> orderItems = menus.stream()
                .map(dto -> {
                    Menu menu = menuRepository.findById(dto.getMenuId())
                            .orElseThrow(() -> new BaseException(ErrorCode.MENU_NOT_FOUND));

                    // 주문한 가게와 메뉴의 가게가 일치하는지 확인.
                    if (!menu.getStore().getId().equals(storeId)) {
                        throw new BaseException(ErrorCode.MENU_NOT_SELECTED_STORE);
                    }

                    return new OrderItem(order, menu, dto.getQuantity());
                })
                .toList();

        orderItems.forEach(order::addOrderItem); // Order 에 OrderItem 추가

        order.calculateTotalPrice(); // 총 주문 금액 계산

        // 5. 주문 저장 (Cascade 설정으로 OrderItem 도 자동 저장)
        orderRepository.save(order);

        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse withdrawOrder(Long userId, Long storeId, Long orderId) {
        // 1. 주문 정보를 가져온다.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(ErrorCode.ORDER_NOT_FOUND));

        // 2. 가져온 주문 정보와 요청한 가게가 같은지 확인
        if (!order.getStore().getId().equals(storeId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        // 3. 주문이 해당 사용자의 것인지 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_ACCESS_TO_ORDER);
        }

        // 3. 주문이 취소 가능한 상태인지 확인 (예: 이미 조리 중이면 취소 불가)
        if (order.getStatus() != OrderStatus.ORDER_RECEIVED) {
            throw new BaseException(ErrorCode.CAN_NOT_BE_ORDER);
        }

        // 4. 주문 상태를 취소로 변경
        order.updateStatus(OrderStatus.CANCELED);

        // 5. 변경 사항 저장
        orderRepository.save(order);

        return new OrderResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable)
                .map(OrderResponse::new);
    }
}