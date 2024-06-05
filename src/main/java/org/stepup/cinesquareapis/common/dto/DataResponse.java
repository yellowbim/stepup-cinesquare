package org.stepup.cinesquareapis.common.dto;

import lombok.Getter;
import lombok.Setter;

// DB 단 건 조회 시 사용
@Getter
@Setter
public class DataResponse<T> {
    private T data;
}