package org.stepup.cinesquareapis.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse<T> {
    private T list;
    private int page;
    private int size;
    private Long totalCount;
    private int lastPage;

    public PageResponse(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
