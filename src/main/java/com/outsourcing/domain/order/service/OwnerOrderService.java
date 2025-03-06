package com.outsourcing.domain.order.service;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.order.dto.OrderResponse;
import com.outsourcing.domain.order.entity.Order;
import com.outsourcing.domain.order.enums.OrderStatus;
import com.outsourcing.domain.order.repository.OrderRepository;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.repository.StoreRepository;
import com.outsourcing.domain.user.entity.Owner;
import com.outsourcing.domain.user.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerOrderService {

    private final OrderRepository orderRepository;
    private final OwnerRepository ownerRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public OrderResponse cookingOrder(Long ownerId, Long storeId, Long orderId) {
        // 1. 사장님의 정보를 가져온다.
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 2. 가게 정보를 가져온다.
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));

        // 3. 해당 가게가 사장님의 가게인지 확인.
        if (!store.getOwner().getId().equals(owner.getId())) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        // 4. 상태값이 주문접수인 정보를 가져온다.
        Order order = orderRepository.findByIdAndStatus(orderId, OrderStatus.ORDER_RECEIVED);

        // 5. 주문 상태값을 Cooking으로 변경한다.
        order.updateStatus(OrderStatus.COOKING);  // 상태 변경
        orderRepository.save(order);  // 상태 업데이트

        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse startDelivery(Long orderId) {
        // 상태값이 조리중인 주문을 불러옴
        Order order = orderRepository.findByIdAndStatus(orderId, OrderStatus.COOKING);

        order.updateStatus(OrderStatus.DELIVERING);  // 상태 변경
        orderRepository.save(order);  // 상태 업데이트

        return new OrderResponse(order);
    }

    @Transactional
    public OrderResponse completeDelivery(Long orderId) {
        // 상태값이 배달중인 주문을 불러옴
        Order order = orderRepository.findByIdAndStatus(orderId, OrderStatus.DELIVERING);

        order.updateStatus(OrderStatus.DELIVERY_COMPLETED);  // 상태 변경
        orderRepository.save(order);  // 상태 업데이트

        return new OrderResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getStoreOrders(Long ownerId, Long storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));

        // 해당 가게가 ownerId의 가게인지 검증
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }

        return orderRepository.findByStoreId(storeId, pageable)
                .map(OrderResponse::new);
    }
}
