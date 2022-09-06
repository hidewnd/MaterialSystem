package com.lyne.module.manager.material.application.service;

import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.infrastructure.domain.EnterMaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;

import java.util.List;

/**
 * @author lyne
 */
public interface RackService {

    // ================
    //      修改操作
    // ================

    /**
     * 新增货架
     * 持久化操作
     * @param rack 初始数据
     * @return rackId
     */
    String generate(MaterialRack rack);

    /**
     * 修改货架
     * @param rack 货架实体
     * @return boolean
     */
    boolean updateRack(MaterialRack rack, String operator);

    /**
     * 删除货架
     * @param rackId 货架ID
     * @param operator 操作原
     * @return boolean
     */
    boolean deleteRack(String rackId, String operator);

    /**
     * 物料入库
     * 持久化操作
     * 1.绑定物料与货架关系
     * 2.若入库量大于货架可用容量，入库量为可用最大容量，并返回剩余数
     * @param matId  物料id
     * @param rackId 货架id
     * @param num    存储数量
     * @return 剩余数 当全部入库成功后返回0
     */
    EnterMaterialVo stored(String rackId, String recordId, String matId, Long num, String operator);

    /**
     * 物料出库
     * 1.修改记录值, 为0时删除
     * @param matId     物料id
     * @param rackId    货架id
     * @param enterId   入库Id
     * @param num       存储数量
     */
    void takeOut(String rackId, String enterId, String matId, Long num, String operator);

    /**
     * 物料出库
     * 通过出库队列出库
     * @param qrcode  物料id
     * @param num    存储数量
     */
    void takeOut(String qrcode, Long num, String operator);


    /**
     * 持久化保存货架对应信息
     * 持久化操作
     * @param rackId rackId
     */
    void saveRack(String rackId);

    /**
     * 随机分配物料详情
     * @param materialId 物料ID
     * @param number     物料数量
     * @return list
     */
    List<MaterialInfo> getMatInfoForOuter(String materialId, Long number);



    // ================
    //      查询操作
    // ================


    /**
     * 获取包含指定物料的货架信息
     * 过滤可用容量为0
     * @param materialId    物料Id
     * @return list
     */
    List<RackVo> getRackForMaterial(String materialId);

    /**
     * 多值分页查询
     *
     * @param size      页大小
     * @param pageSize  页序号
     * @param arg1      条件参数一
     * @param arg2      条件参数二
     * @return PageObject
     */
    PageObject<RackVo> queryList(Integer size, Integer pageSize, String arg1, String arg2);

    /**
     * 单值查询
     * @param rackId rackId
     * @return  vo
     */
    RackVo queryOne(String rackId);

    /**
     * 计算指定货架的当前实际容量
     * @param rackId 货架Id
     * @return long
     */
    Long getPracticalCapacity(String rackId);



}
