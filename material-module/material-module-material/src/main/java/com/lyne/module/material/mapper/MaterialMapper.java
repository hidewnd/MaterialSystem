package com.lyne.module.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyne.common.core.types.Material;
import org.springframework.stereotype.Repository;

/**
 * 物料
 * @author lyne
 */
@Repository
public interface MaterialMapper extends BaseMapper<Material> {

    /**
     * 分页查询
     * @param page page
     * @return list
     */
    IPage<Material> selectByPage(IPage<Material> page);

}
