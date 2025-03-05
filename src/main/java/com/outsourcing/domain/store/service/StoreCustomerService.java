package com.outsourcing.domain.store.service;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.store.dto.response.StoreCustomerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.enums.StoreStatus;
import com.outsourcing.domain.store.repository.StoreRepository;
import com.outsourcing.domain.user.entity.Customer;
import com.outsourcing.domain.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreCustomerService {

    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;
    private final MenuRepository menuRepository;

    // 공통 로직
    private Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }
    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));
    }

    // Customer의 화면에서 운영중인 가게 전체 조회(페이지네이션 적용)
    @Transactional(readOnly = true)
    public Page<StoreCustomerResponse> getAllPage(int page, int size) {

        int adjustedPage = (page > 0) ? page - 1 : 0;
        PageRequest pageable = PageRequest.of(adjustedPage, size);
        Page<Store> storesPage = storeRepository.findByStoreStatus(StoreStatus.OPERATIONAL,pageable);

        return storesPage.map(store -> new StoreCustomerResponse(
                store.getId(),
                store.getStoreName(),
                store.getStoreProfileUrl(),
                store.getStoreAddress(),
                store.getStorePhoneNumber(),
                store.getOpenedAt(),
                store.getClosedAt(),
                store.getMinPrice()
        ));
    }

    // Customer 입장에서 가게 단건 조회
    @Transactional(readOnly = true)
    public StoreResponse getStore(Long storeId) {
        Store store = getStoreById(storeId);

        List<Menu> menus = menuRepository.findByStoreId(storeId);
        return StoreResponse.of(store,menus);
    }
}
