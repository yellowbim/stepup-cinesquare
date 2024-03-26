package org.stepup.cinesquareapis.common.model;

import lombok.Getter;
import lombok.Setter;

// DB 데이터 조회 외 기타 결과를 반환할 시 사용
@Getter
@Setter
public class ResultResponse<T> {
    private T result;
}