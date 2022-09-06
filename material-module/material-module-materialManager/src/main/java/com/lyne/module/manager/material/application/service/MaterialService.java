package com.lyne.module.manager.material.application.service;

import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialType;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;

import java.util.List;
import java.util.Map;

/**
 * 领域服务
 * 物料服务
 *
 * @author lyne
 */
public interface MaterialService {


    // ================
    //      查询操作
    // ================

    /**
     * 获取进出库记录中的物料状态
     *
     * @param recordId   记录ID
     * @param materialId 物料ID
     * @return 状态信息
     */
    String getMaterialState(String recordId, String materialId);

    /**
     * 获取物料位置
     *
     * @param rackId     货架ID
     * @param recordId   入库ID
     * @param materialId 物料ID
     * @return 位置信息
     */
    String getLocation(String rackId, String recordId, String materialId);

    /**
     * 获取物料位置
     *
     * @param rackId     货架ID
     * @param materialId 物料ID
     * @return 位置信息
     */
    List<String> getLocation(String rackId, String materialId);

    /**
     * 查询物料库存
     * @param materialId 物料ID
     * @return 库存
     */
    long queryCapacity(String materialId);

    /**
     * 查询所有物料库存
     * @return map
     */
    Map<String, Long> queryAllCapacity();

    /**
     * 物料详情
     * 单值查询
     *
     * @param rackId        货架Id
     * @param recordId      入库Id
     * @param materialId    物料Id
     * @return info
     */
    MaterialInfo queryInfo(String rackId, String recordId, String materialId);

    /**
     * 单值查询
     * @param clazz 查询实体类
     * @param id 实体Id
     * @return t
     */
    <T> T queryOne(Class<T> clazz, String id);

    /**
     * 分页查询
     * @param clazz 查询实体类
     * @param size 页大小
     * @param page 页序号
     * @param arg1 条件参数一
     * @param arg2 条件参数二
     * @return list
     */
    <T> PageObject<T> queryList(Class<T> clazz, Integer size, Integer page, String arg1, String arg2);

    /**
     * 查询对应物料的所有相关数据
     * @param materialId 物料id
     * @return list
     */
    List<MaterialInfo> queryInfoList(String materialId);

    PageObject<MaterialInfo> queryInfoListByArg(String depotId, String rackId, String materialId, Integer page, Integer size);

    // ================
    //      修改操作
    // ================

    /**
     * 新增物料
     * @param material 初始数据
     * @return materialId
     */
    String generalMaterial(Material material);

    /**
     * 新增物料类型
     * @param type 初始数据
     * @return typeId
     */
    String generalType(MaterialType type);

    /**
     * 更新物料信息
     * @param material  物料实体
     * @return boolean
     */
    boolean updateMaterial(Material material, String operator);

    /**
     * 更新物料类型信息
     * @param type      物料类型实体
     * @return boolean
     */
    boolean updateMaterialType(MaterialType type, String operator);

    /**
     * 删除物料信息
     * @param materialId    物料ID
     * @param operator      操作人员
     * @return boolean
     */
    boolean deleteMaterial(String materialId, String operator);

    /**
     * 删除物料类型信息
     * @param typeId        物料类型ID
     * @param operator      操作人员
     * @return boolean
     */
    boolean deleteMaterialType(String typeId, String operator);


}
