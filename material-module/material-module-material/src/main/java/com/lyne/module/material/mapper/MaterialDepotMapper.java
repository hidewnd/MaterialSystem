package com.lyne.module.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.types.MaterialRack;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 仓库
 *
 * @author lyne
 */
@Repository
public interface MaterialDepotMapper extends BaseMapper<MaterialDepot> {

    /**
     * 获取指定仓库中所有货架
     * @param depotId 仓库ID
     * @return list
     */
    List<MaterialRack> getRack(String depotId);

    /**
     * 获取指定货架
     * @param depotId 仓库
     * @param rackId 货架
     * @return rack
     */
    MaterialRack getRackByRack(@Param("depotId") String depotId, @Param("rackId") String rackId);

    /**
     * 分页查询
     * @param page page
     * @return list
     */
    IPage<MaterialDepot> selectByPage(IPage<MaterialDepot> page);

}

