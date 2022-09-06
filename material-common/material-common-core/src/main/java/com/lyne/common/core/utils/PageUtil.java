package com.lyne.common.core.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 分页工具
 * @author lyne
 */
public class PageUtil {


    public static <T> Page<T> instancePage(int page, int size) {
        int current = page <= 0 ? 1 : page;
        int defaultSize = size <= 0 ? 10 : size;
        return new Page<>(current, defaultSize);
    }
}
