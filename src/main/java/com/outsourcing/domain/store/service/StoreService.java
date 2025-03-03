package com.outsourcing.domain.store.service;

import com.outsourcing.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

//    @Transactional
//    public StoreResponseForOwner createStore(
//            CustomUserDetails currentOwner,
//            @Valid CreateStoreRequest request
//    ) {
//
//    }
}
