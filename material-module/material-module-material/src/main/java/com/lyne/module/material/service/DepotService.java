package com.lyne.module.material.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.types.MaterialRack;

import java.util.List;

/**
 * 仓库服务
 *
 * @author lyne
 */
public interface DepotService extends IService<MaterialDepot> {
    /**
     * 仓库绑定货架
     *
     * @param depotId 仓库ID
     * @param rackId  货架ID
     * @return boolean
     */
    boolean bindRack(String depotId, String rackId);

    /**
     * 仓库取消绑定货架
     *
     * @param depotId 仓库ID
     * @param rackId  货架ID
     * @return boolean
     */
    boolean removeRack(String depotId, String rackId);

    /**
     * 获取仓库总货架
     *
     * @param depotId 仓库ID
     * @return RAC
     */
    List<MaterialRack> getRackList(String depotId);

    /**
     * 获取仓库指定货架
     *
     * @param depotId 仓库ID
     * @param rackId  货架ID
     * @return RAC
     */
    MaterialRack getRackById(String depotId, String rackId);

    /**
     * 多数据查询
     * @param wrapper wrapper
     * @return list
     */
    List<MaterialDepot> queryList(Wrapper<MaterialDepot> wrapper);


    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<MaterialDepot> queryByPage(int page, int size);

}
