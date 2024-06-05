package org.stepup.cinesquareapis.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageResponse2<T> {
    private List<T> list;
    private int page;
    private int size;
    private Long totalCount;
    private int lastPage;

    public PageResponse2(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public PageResponse2(List<T> list, int page, int size, long totalCount, int lastPage) {
        this.list = list;
        this.page = page;
        this.size = size;
        this.totalCount = totalCount;
        this.lastPage = lastPage;
    }

    public PageResponse2(Page pagedData, int page, int size) {
        this.list = pagedData.getContent(); // 데이터
        this.page = page; // 시작 페이지
        this.size = size; // 페이지당 조회 개수
        this.totalCount = pagedData.getTotalElements(); // 마지막 페이지
        this.lastPage = pagedData.getTotalPages() == 0 ? 1 : pagedData.getTotalPages(); // 총 건수
    }
}