package com.lyne.module.manager.material.domain.material.repository;

import com.lyne.common.core.types.Material;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;

import java.util.List;

/**
 * 持久化接口
 * 物料信息
 *
 * @author lyne
 */
public interface MaterialRepository {

    /**
     * 异步添加一个 material
     * @param material material
     */
    void insert(Material material);

    /**
     * 同步添加一个 material
     * @param material material
     * @return vo
     */
    MaterialVo insertSync(Material material);

    /**
     * 持久化保存 material
     * @param vo vo
     */
    void save(MaterialVo vo);

    /**
     * 持久化删除 material
     * @param vo vo
     */
    void delete(MaterialVo vo);

    /**
     * 持久化保存物料库存
     * @param info info
     */
    void saveInfo(MaterialInfo info);

    /**
     * 批量持久化保存物料库存
     * @param list info
     */
    void saveInfo(List<MaterialInfo> list);


    /**
     * 删除物料详情
     * @param info info
     */
    void deleteInfo(MaterialInfo info);

}
