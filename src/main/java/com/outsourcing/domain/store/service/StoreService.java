package com.outsourcing.domain.store.service;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.store.dto.request.*;
import com.outsourcing.domain.store.dto.response.StoreCustomerResponse;
import com.outsourcing.domain.store.dto.response.StoreOwnerResponse;
import com.outsourcing.domain.store.dto.response.StoreResponse;
import com.outsourcing.domain.store.entity.Store;
import com.outsourcing.domain.store.enums.StoreStatus;
import com.outsourcing.domain.store.repository.StoreRepository;
import com.outsourcing.domain.user.dto.request.customer.UpdateProfileUrlRequest;
import com.outsourcing.domain.user.entity.Owner;
import com.outsourcing.domain.user.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final OwnerRepository ownerRepository;
    private final MenuRepository menuRepository;

    // 공통 로직 : 사장님 이메일로 등록된 Owner 확인
    private Owner getOwnerByEmail(String ownerEmail) {
        return ownerRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // 공통 로직 : 가게 존재 확인
    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));
    }

    @Transactional
    public StoreOwnerResponse createStore(
            String ownerEmail,
            CreateStoreRequest request
    ) {
        Owner owner = getOwnerByEmail(ownerEmail);

        if (storeRepository.countStoreByOwner(owner) >= 3) {
            throw new BaseException(ErrorCode.MAX_STORE_LIMIT_REACHED);
        }

        Store store = new Store(
                request.getStoreName(),
                request.getStoreProfileUrl(),
                request.getStoreAddress(),
                request.getStorePhoneNumber(),
                request.getOpenedAt(),
                request.getClosedAt(),
                request.getMinPrice(),
                StoreStatus.OPERATIONAL
        );
        store.setOwner(owner);

        Store saved = storeRepository.save(store);
        return StoreOwnerResponse.of(saved);
    }

    //Customer 입장에서의 가게 전체 조회(운영중인 가게만 조회 가능)
    @Transactional(readOnly = true)
    public List<StoreCustomerResponse> findAllStoresForCustomer() {

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

    // Owner 입장에서의 가게 전체 조회(모든 status 조회 가능)
    @Transactional(readOnly = true)
    public List<StoreOwnerResponse> findAllStoresForOwner(StoreStatus storeStatus) {

        List<Store> stores = storeRepository.findByStoreStatus(storeStatus);

        List<StoreOwnerResponse> dtoList = new ArrayList<>();
        for (Store store : stores) {
            dtoList.add(new StoreOwnerResponse(
                    store.getId(),
                    store.getStoreName(),
                    store.getStoreProfileUrl(),
                    store.getStoreAddress(),
                    store.getStorePhoneNumber(),
                    store.getOpenedAt(),
                    store.getClosedAt(),
                    store.getStoreStatus(),
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

    @Transactional
    public StoreOwnerResponse updateStoreName(
            Long storeId,
            UpdateNameRequest request,
            String ownerEmail) {

        Owner owner = getOwnerByEmail(ownerEmail);

        Store store = getStoreById(storeId);

        store.updateStoreName(request.getStoreName());
        return StoreOwnerResponse.of(store);
    }

    @Transactional
    public StoreOwnerResponse updateProfileUrl(
            Long storeId,
            UpdateProfileUrlRequest request,
            String ownerEmail) {

        Owner owner = getOwnerByEmail(ownerEmail);

        Store store = getStoreById(storeId);

        store.updateProfileUrl(request.getNewProfileUrl());
        return StoreOwnerResponse.of(store);
    }

    @Transactional
    public StoreOwnerResponse updateStoreAddress(
            Long storeId,
            UpdateAddressRequest request,
            String ownerEmail) {

        Owner owner = getOwnerByEmail(ownerEmail);

        Store store = getStoreById(storeId);

        store.updateStoreAddress(request.getStoreAddress());
        return StoreOwnerResponse.of(store);
    }

    @Transactional
    public StoreOwnerResponse updateStorePhoneNumber(
            Long storeId,
            UpdateNumberRequest request,
            String ownerEmail) {

        Owner owner = getOwnerByEmail(ownerEmail);

        Store store = getStoreById(storeId);

        store.updateStorePhoneNumber(request.getStorePhoneNumber());
        return StoreOwnerResponse.of(store);
    }

    @Transactional
    public StoreOwnerResponse updateStoreHours(
            Long storeId,
            UpdateStoreHoursRequest request,
            String ownerEmail) {

        Owner owner = getOwnerByEmail(ownerEmail);

        Store store = getStoreById(storeId);

        // null 값 검증
        if (request.getOpenedAt() == null || request.getClosedAt() == null) {
            throw new BaseException(ErrorCode.MISSING_STORE_HOURS);
        }

        store.updateStoreHours(request.getOpenedAt(), request.getClosedAt());
        return StoreOwnerResponse.of(store);
    }

    @Transactional
    public StoreOwnerResponse updateStoreStatus(
            Long storeId,
            UpdateStatusRequest request,
            String ownerEmail) {

        Owner owner = getOwnerByEmail(ownerEmail);

        Store store = getStoreById(storeId);

        //지정된 enum타입의 유효값 체크
        StoreStatus storeStatus = request.getStoreStatus();
        if (!StoreStatus.isValidStoreStatus(storeStatus.name())) {
            throw new BaseException(ErrorCode.INVALID_STORE_STATUS);
        }

        store.updateStoreStatus(request.getStoreStatus());
        return StoreOwnerResponse.of(store);
    }

    @Transactional
    public StoreOwnerResponse updateMinPrice(
            Long storeId,
            UpdateMinPriceRequest request,
            String ownerEmail) {

        Owner owner = getOwnerByEmail(ownerEmail);

        Store store = getStoreById(storeId);

        store.updateMinPrice(request.getMinPrice());
        return StoreOwnerResponse.of(store);
    }

    @Transactional
    public void deleteById(Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new BaseException(ErrorCode.STORE_NOT_FOUND)
        );

        store.setStoreStatus(StoreStatus.SHUTDOWN);

        // 사장님의 등록된 가게의 카운트를 -1 해줘야함.
//        Owner owner = store.getOwner();
//        owner.setStoreCount(owner.getStoreCount()-1);
//        ownerRepository.save(owner);

        storeRepository.save(store);
    }
}
