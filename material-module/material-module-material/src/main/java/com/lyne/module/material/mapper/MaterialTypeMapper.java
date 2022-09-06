package com.lyne.module.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialType;
import org.springframework.stereotype.Repository;

/**
 * 物料类型
 * @author lyne
 */
@Repository
public interface MaterialTypeMapper extends BaseMapper<MaterialType> {

    /**
     * 分页查询
     * @param page page
     * @return list
     */
    IPage<MaterialType> selectByPage(IPage<Material> page);
}
