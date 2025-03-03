package com.outsourcing.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateImageRequest {

    @NotBlank
    private String storeProfileUrl;
}
