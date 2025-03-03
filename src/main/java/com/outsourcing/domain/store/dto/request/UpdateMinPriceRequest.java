package com.outsourcing.domain.store.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdateMinPriceRequest {

    @NotNull
    @Min(0)
    private BigDecimal minPrice;
}
