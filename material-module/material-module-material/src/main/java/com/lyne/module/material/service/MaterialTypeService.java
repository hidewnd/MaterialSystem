package com.lyne.module.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.MaterialType;

/**
 * 物料类型服务
 *
 * @author lyne
 */
public interface MaterialTypeService extends IService<MaterialType> {
    /**
     * 通过类型名查询
     *
     * @param name 类型名
     * @return materialType
     */
    MaterialType queryByName(String name);

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<MaterialType> queryByPage(int page, int size);

}
