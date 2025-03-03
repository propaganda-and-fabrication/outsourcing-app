package com.outsourcing.domain.store.dto.request;

import com.outsourcing.domain.store.enums.StoreStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateStatusRequest {

    @NotBlank
    private StoreStatus storeStatus;
}