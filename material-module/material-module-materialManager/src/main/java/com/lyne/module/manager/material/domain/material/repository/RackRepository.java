package com.lyne.module.manager.material.domain.material.repository;

import com.lyne.common.core.types.MaterialRack;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;

/**
 * 持久化接口
 * 货架信息
 *
 * @author lyne
 */
public interface RackRepository {
    /**
     * 持久化实体信息
     *
     * @param vo vo
     */
    void save(RackVo vo);

    /**
     * 持久化实体关系
     *
     * @param vo vo
     */
    void saveRelation(RackVo vo);

    /**
     * 持久化删除实体
     *
     * @param vo vo
     */
    void delete(RackVo vo);

    /**
     * 添加实体信息
     *
     * @param vo vo
     */
    void insert(MaterialRack vo);

    /**
     * 同步添加实体信息
     *
     * @param rack rack
     * @return rackId
     */
    String insertSync(MaterialRack rack);

    /**
     * 数据校验, 校验当前容量值
     * 对货架容量进行修正更新
     * @param vo vo
     */
    void modifiedCapacity(RackVo vo);
}
