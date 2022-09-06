package com.lyne.module.manager.material.infrastructure.utils;


import com.lyne.common.core.base.PageObject;
import org.springframework.data.domain.PageImpl;

/**
 *
 * @author lyne
 */
public class PageUtil {

    public static <T> PageObject<T> getPage(PageImpl<T> page) {
        PageObject<T> obj = new PageObject<>();
        obj.setTotal(page.getTotalElements());
        obj.setTotalPage(page.getTotalPages());
        obj.setPage(page.getNumber() + 1);
        obj.setElement(page.getContent());
        return obj;
    }
}
