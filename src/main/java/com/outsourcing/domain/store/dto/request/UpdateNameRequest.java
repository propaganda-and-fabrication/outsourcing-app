package com.outsourcing.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateNameRequest {

    @NotBlank
    private String storeName;
}
