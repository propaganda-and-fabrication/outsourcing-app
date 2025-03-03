package com.outsourcing.domain.store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdateAddressRequest {

    @NotBlank
    @Pattern(regexp = "^[가-힣0-9\s-]+$  # 한글, 숫자, 공백, 하이픈", message = "주소 형식이 올바르지 않습니다.")
    private String storeAddress;
}
