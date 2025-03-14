package com.outsourcing.domain.store.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
public class CreateStoreRequest {

    @NotBlank
    private String storeName;

    @NotBlank
    private String storeProfileUrl;

    @NotBlank
    @Pattern(regexp = "^[가-힣0-9\s-]+$", message = "주소 형식이 올바르지 않습니다.")
    private String storeAddress;

    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String storePhoneNumber;

    @NotNull
    @Min(0)
    private BigDecimal minPrice;

    @NotNull
    private LocalTime openedAt;

    @NotNull
    private LocalTime closedAt;
}
