package com.lyne.module.material.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialRack;

import java.util.List;

/**
 * 货架服务
 *
 * @author lyne
 */

public interface RackService extends IService<MaterialRack> {
    /**
     * 物料入库
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存库数
     * @return true
     */
    boolean bindMaterial(String rackId, String recordId, String materialId, Long stock);

    /**
     * 物料批量入库
     * @param list list
     * @return true
     */
    boolean bindMaterial(List<Material> list);

    /**
     * 物料出库
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存库数
     * @return boolean
     */
    boolean removeMaterial(String rackId, String recordId, String materialId, Long stock);

    /**
     * 修改物料库存数
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @param stock         存库数
     * @return boolean
     */
    boolean updateMaterial(String rackId, String recordId, String materialId, Long stock);

    /**
     * 批量修改物料库存数
     * @param list list
     * @return boolean
     */
    boolean updateMaterial(List<Material> list);

    /**
     * 获取该货架中的库存物料
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return list
     */
    List<Material> getMaterialList(String rackId, String recordId, String materialId);

    /**
     * 获取已入库物料
     * @param rackId rackId
     * @return list
     */
    List<Material> getMaterialList(String rackId);

    /**
     * 获取物料列表
     * @return all material
     */
    List<Material> getAllMaterial();

    /**
     * 获取物料Id
     * @param rackId rackId
     * @return list
     */
    List<String> getMaterialIds(String rackId);

    /**
     * 多值查询
     * @param wrapper 条件
     * @return list
     */
    List<MaterialRack> queryList(Wrapper<MaterialRack> wrapper);

    /**
     * 查询指定的物料信息
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return list
     */
    Material getMaterial(String rackId, String recordId, String materialId);

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<MaterialRack> queryByPage(int page, int size);
    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<Material> queryStockByPage(int page, int size);
}
