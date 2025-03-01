package com.outsourcing.domain.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreStatus {
    //error를 따로 던져야 하는지 고민중입니다.
    OPERATIONAL, SHUTDOWN;
}
