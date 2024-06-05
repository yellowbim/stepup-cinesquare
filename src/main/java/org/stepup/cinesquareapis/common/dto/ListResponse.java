package org.stepup.cinesquareapis.common.dto;

import lombok.Getter;
import lombok.Setter;

// DB 목록 조회 시 사용
@Getter
@Setter
public class ListResponse<T> {
    private T list;
}