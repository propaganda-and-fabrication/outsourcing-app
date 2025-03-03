package com.outsourcing.domain.store.controller;

import com.outsourcing.common.response.Response;
import com.outsourcing.domain.store.dto.request.CreateStoreRequestDTO;
import com.outsourcing.domain.store.dto.response.StoreResponseDto;
import com.outsourcing.domain.store.service.StoreService;
import com.outsourcing.domain.user.entity.Owner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
}
