package com.lyne.module.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.module.material.mapper.MaterialTypeMapper;
import com.lyne.module.material.service.MaterialTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 物料类型服务
 *
 * @author lyne
 */
@Service
public class MaterialTypeServiceImpl extends ServiceImpl<MaterialTypeMapper, MaterialType>
        implements MaterialTypeService {
    @Resource
    private MaterialTypeMapper typeMapper;

    /**
     * 通过类型名查询
     *
     * @param name 类型名
     * @return materialType
     */
    @Override
    public MaterialType queryByName(String name) {
        QueryWrapper<MaterialType> wrapper = new QueryWrapper<>();
        wrapper.eq("type_name", name);
        wrapper.eq("status", 0);
        wrapper.eq("del_flag", 0);
        return typeMapper.selectOne(wrapper);
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<MaterialType> queryByPage(int page, int size) {
        PageObject<MaterialType> pageObject = new PageObject<>();
        IPage<MaterialType> iPage = typeMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }

}
