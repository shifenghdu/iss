package com.db.iss.admin.domain.common;

/**
 * Created by andy on 16/7/15.
 * @author andy.shif
 * 分页信息
 */
public class Page {

    /**
     * 每页数量
     */
    private Long pageSize;

    /**
     * 总页数
     */
    private Long pageCount;

    /**
     * 当前页数
     */
    private Long current;

    /**
     * 总条数
     */
    private Long totalCount;


    public Page(Long current,Long pageSize){
        this.current = current;
        this.pageSize = pageSize;
    }

    /**
     * 获取总条数
     * @return
     */
    public Long getTotalCount() {
        return totalCount;
    }

    /**
     * 设置总条数
     * @param totalCount
     */
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        this.pageCount = (totalCount + pageSize - 1) / pageSize;
    }

    /**
     * 获取page页大小
     * @return
     */
    public Long getPageSize() {
        return pageSize;
    }

    /**
     * 获取page数量
     * @return
     */
    public Long getPageCount() {
        return pageCount;
    }

    /**
     * 获取当前页
     * @return
     */
    public Long getCurrent() {
        return current;
    }
}
