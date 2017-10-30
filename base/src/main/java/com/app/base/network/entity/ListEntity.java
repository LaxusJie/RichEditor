package com.app.base.network.entity;

import java.util.List;

/**
 * desc：集合实体
 * author：haojie
 * date：2017-06-24
 */
public class ListEntity<T> {
    private List<T> list;
    private PageEntity page;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public PageEntity getPage() {
        return page;
    }

    public void setPage(PageEntity page) {
        this.page = page;
    }
}
