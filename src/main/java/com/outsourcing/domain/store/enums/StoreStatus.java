package com.outsourcing.domain.store.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreStatus {

    //가게 상태를 [운영중, 임시 휴업(운영시간 이후의 경우도 포함), 폐업]으로 설정하는 것이 좋을 것 같아 변경합니다!
    OPERATIONAL,
    TEMPORARY_CLOSED,
    SHUTDOWN;
}