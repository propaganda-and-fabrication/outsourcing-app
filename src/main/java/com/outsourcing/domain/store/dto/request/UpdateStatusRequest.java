package com.outsourcing.domain.store.dto.request;

import com.outsourcing.domain.store.enums.StoreStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateStatusRequest {

    @NotNull
    private StoreStatus storeStatus;
}