package com.outsourcing.domain.store.service;

import com.outsourcing.common.exception.BaseException;
import com.outsourcing.common.exception.ErrorCode;
import com.outsourcing.domain.menu.entity.Menu;
import com.outsourcing.domain.menu.repository.MenuRepository;
import com.outsourcing.domain.store.dto.request.*;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreOwnerService {

    private final StoreRepository storeRepository;
    private final OwnerRepository ownerRepository;
    private final MenuRepository menuRepository;

    // 공통 로직
    private Owner getOwnerById(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseException(ErrorCode.STORE_NOT_FOUND));
    }

    private void validateOwner(Long ownerId, Store store) {
        if (!store.getOwner().getId().equals(ownerId)) {
            throw new BaseException(ErrorCode.UNAUTHORIZED_STORE);
        }
    }

    // 가게 생성
    @Transactional
    public StoreOwnerResponse createStore(
            Long ownerId,
            CreateStoreRequest request
    ) {
        Owner owner = getOwnerById(ownerId);

        if (owner.getStoreCount() >= 3) {
            throw new BaseException(ErrorCode.MAX_STORE_LIMIT_REACHED);
        }

        Store store = new Store(
                owner,
                request.getStoreName(),
                request.getStoreProfileUrl(),
                request.getStoreAddress(),
                request.getStorePhoneNumber(),
                request.getOpenedAt(),
                request.getClosedAt(),
                request.getMinPrice(),
                StoreStatus.OPERATIONAL
        );

        Store saved = storeRepository.save(store);
        return StoreOwnerResponse.of(saved);
    }

    //내 가게 전체 조회
    @Transactional(readOnly = true)
    public List<StoreOwnerResponse> getAll(Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        List<Store> stores = storeRepository.findByOwnerId(ownerId);

        return stores.stream()
                .map(store -> new StoreOwnerResponse(
                        store.getId(),
                        store.getStoreName(),
                        store.getStoreProfileUrl(),
                        store.getStoreAddress(),
                        store.getStorePhoneNumber(),
                        store.getOpenedAt(),
                        store.getClosedAt(),
                        store.getStoreStatus(),
                        store.getMinPrice()))
                .toList();
    }

    //내 가게 단건 조회
    @Transactional(readOnly = true)
    public StoreResponse getStore(Long ownerId, Long storeId) {
        Owner owner = getOwnerById(ownerId);
        Store store = storeRepository.findByIdWithMenus(storeId);

        validateOwner(ownerId,store);

        List<Menu> menus = store.getMenus();
        return StoreResponse.of(store,menus);
    }

    //가게 이름 수정
    @Transactional
    public StoreOwnerResponse updateStoreName(
            Long storeId,
            UpdateNameRequest request,
            Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        Store store = getStoreById(storeId);
        validateOwner(ownerId,store);

        store.updateStoreName(request.getStoreName());
        return StoreOwnerResponse.of(store);
    }

    // 가게 사진 변경
    @Transactional
    public StoreOwnerResponse updateProfileUrl(
            Long storeId,
            UpdateProfileUrlRequest request,
            Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        Store store = getStoreById(storeId);
        validateOwner(ownerId,store);

        store.updateProfileUrl(request.getNewProfileUrl());
        return StoreOwnerResponse.of(store);
    }

    // 가게 주소 수정
    @Transactional
    public StoreOwnerResponse updateStoreAddress(
            Long storeId,
            UpdateAddressRequest request,
            Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        Store store = getStoreById(storeId);
        validateOwner(ownerId,store);

        store.updateStoreAddress(request.getStoreAddress());
        return StoreOwnerResponse.of(store);
    }

    // 가게 전화번호 수정
    @Transactional
    public StoreOwnerResponse updateStorePhoneNumber(
            Long storeId,
            UpdateNumberRequest request,
            Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        Store store = getStoreById(storeId);
        validateOwner(ownerId,store);

        store.updateStorePhoneNumber(request.getStorePhoneNumber());
        return StoreOwnerResponse.of(store);
    }

    // 가게 운영시간 수정
    @Transactional
    public StoreOwnerResponse updateStoreHours(
            Long storeId,
            UpdateStoreHoursRequest request,
            Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        Store store = getStoreById(storeId);
        validateOwner(ownerId,store);

        // null 값 검증
        if (request.getOpenedAt() == null || request.getClosedAt() == null) {
            throw new BaseException(ErrorCode.MISSING_STORE_HOURS);
        }

        store.updateStoreHours(request.getOpenedAt(), request.getClosedAt());
        return StoreOwnerResponse.of(store);
    }

    // 가게 상태 수정
    @Transactional
    public StoreOwnerResponse updateStoreStatus(
            Long storeId,
            UpdateStatusRequest request,
            Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        Store store = getStoreById(storeId);
        validateOwner(ownerId,store);

        //지정된 enum타입의 유효값 체크
        StoreStatus storeStatus = request.getStoreStatus();
        if (!StoreStatus.isValidStoreStatus(storeStatus.name())) {
            throw new BaseException(ErrorCode.INVALID_STORE_STATUS);
        }

        store.updateStoreStatus(request.getStoreStatus());
        return StoreOwnerResponse.of(store);
    }

    // 가게 최소주문 금액 수정
    @Transactional
    public StoreOwnerResponse updateMinPrice(
            Long storeId,
            UpdateMinPriceRequest request,
            Long ownerId) {

        Owner owner = getOwnerById(ownerId);
        Store store = getStoreById(storeId);
        validateOwner(ownerId,store);

        if (request.getMinPrice() == null) {
            throw new BaseException(ErrorCode.MISSING_MIN_PRICE);
        }

        store.updateMinPrice(request.getMinPrice());
        return StoreOwnerResponse.of(store);
    }

    // 가게 삭제(운영상태만 SHUTDOWN으로 변경, owner가 소유한 가게 수 -1 처리)
    @Transactional
    public void deleteById(Long storeId, Long ownerId) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new BaseException(ErrorCode.STORE_NOT_FOUND)
        );
        store.setStoreStatus(StoreStatus.SHUTDOWN);
        storeRepository.save(store);

         //사장님의 등록된 가게의 카운트를 -1 해줘야함.
        Owner owner = getOwnerById(ownerId);
        validateOwner(ownerId,store);

        owner.setStoreCount(owner.getStoreCount()-1);
        ownerRepository.save(owner);
    }
}
