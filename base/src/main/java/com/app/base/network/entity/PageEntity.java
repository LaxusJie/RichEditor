package com.app.base.network.entity;

/**
 * desc：分页实体
 * author：haojie
 * date：2017-06-06
 */
public class PageEntity {
    //总条数
    private int totalCount;
    //当前页
    private int currentPage;
    //每页条数
    private int pageSize;
    //总页数
    private int pageCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
