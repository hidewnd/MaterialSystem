package com.lyne.common.core.base;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 * @author lyne
 */
public class PageObject<T> implements Serializable {
    /**
     * 总数
     */
    private Long total;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 当前页数
     */
    private Integer page;
    /**
     * 当前页元素列表
     */
    private List<T> element;

    public PageObject() {
    }


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<T> getElement() {
        return element;
    }

    public void setElement(List<T> element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "Page{" +
                "total=" + total +
                ", totalPage=" + totalPage +
                ", page=" + page +
                ", element=" + element +
                '}';
    }

    public void setPageObject(IPage<T> iPage) {
        this.setElement(iPage.getRecords());
        this.setTotalPage((int) iPage.getPages());
        this.setPage((int) iPage.getCurrent());
        this.setTotal(iPage.getTotal());
    }
}
