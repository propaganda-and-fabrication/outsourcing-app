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

    //Customer 입장에서의 가게 전체 조회(운영중인 가게만 조회 가능)
    @Transactional(readOnly = true)
    public List<StoreCustomerResponse> getStores(Long customerId) {

        Customer customer = getCustomerById(customerId);

        List<Store> stores = storeRepository.findByStoreStatus(StoreStatus.OPERATIONAL);

        List<StoreCustomerResponse> dtoList = new ArrayList<>();
        for (Store store : stores) {
            dtoList.add(new StoreCustomerResponse(
                    store.getId(),
                    store.getStoreName(),
                    store.getStoreProfileUrl(),
                    store.getStoreAddress(),
                    store.getStorePhoneNumber(),
                    store.getOpenedAt(),
                    store.getClosedAt(),
                    store.getMinPrice())
            );
        }
        return dtoList;
    }

    @Transactional(readOnly = true)
    public StoreResponse findById(Long storeId) {
        Store store = getStoreById(storeId);

        List<Menu> menus = menuRepository.findByStoreId(storeId);
        return StoreResponse.of(store,menus);
    }
}
