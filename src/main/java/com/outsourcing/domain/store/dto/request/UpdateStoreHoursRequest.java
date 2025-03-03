package com.outsourcing.domain.store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class UpdateStoreHoursRequest {

    @NotNull
    private LocalTime openedAt;

    @NotNull
    private LocalTime closedAt;
}
