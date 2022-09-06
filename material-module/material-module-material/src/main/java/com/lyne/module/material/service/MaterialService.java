package com.lyne.module.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.Material;

/**
 * 物料服务
 * @author lyne
 */
public interface MaterialService extends IService<Material> {

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<Material> queryByPage(int page, int size);
}
