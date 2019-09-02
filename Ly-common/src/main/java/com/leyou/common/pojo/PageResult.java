package com.leyou.common.pojo;


import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private Long total;

    private Long TotalPage;

    private List<T> items;

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Long totalPage, List<T> items) {
        this.total = total;
        TotalPage = totalPage;
        this.items = items;
    }
}
