package com.lyne.module.manager.material.domain.material.repository;

import com.lyne.common.core.types.MaterialDepot;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;

/**
 * 持久层
 * 仓库id
 *
 * @author lyne
 */
public interface DepotRepository {

    /**
     * 持久化保存
     * @param vo vo
     */
    void save(DepotVo vo);

    /**
     * 持久化删除 删除所有记录
     * @param vo vo
     */
    void delete(DepotVo vo);

    /**
     * 添加新实体
     * @param vo vo
     */
    void insert(MaterialDepot vo);

    /**
     * 同步获取
     * @param vo 值对象
     * @return depotId
     */
    String insertSync(MaterialDepot vo);
}
