package com.lyne.module.material.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.module.material.mapper.MaterialMapper;
import com.lyne.module.material.service.MaterialService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 物料服务
 *
 * @author lyne
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Resource
    private MaterialMapper materialMapper;

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<Material> queryByPage(int page, int size) {
        IPage<Material> iPage = materialMapper.selectByPage(PageUtil.instancePage(page, size));
        PageObject<Material> pageObject = new PageObject<>();
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }
}
