package com.lyne.module.manager.material.domain.material.repository;

import com.lyne.common.core.types.MaterialType;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialTypeVo;

/**
 * 持久化接口
 * 物料类型信息
 *
 * @author lyne
 */
public interface MaterialTypeRepository {

    /**
     * 异步-添加物料类型
     * @param vo materialType
     */
    void insert(MaterialType vo);

    /**
     * 同步-添加物料类型
     * @param vo materialType
     * @return typeId
     */
    MaterialTypeVo insertSync(MaterialType vo);

    /**
     * 持久化保存
     * @param vo vo
     */
    void save(MaterialTypeVo vo);

    /**
     * 持久化删除
     * @param vo vo
     */
    void delete(MaterialTypeVo vo);
}
