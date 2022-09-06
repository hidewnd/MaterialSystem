package com.lyne.module.manager.material.application.service;

import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;

import java.util.List;

/**
 * @author lyne
 */
public interface DepotService {
    /**
     * 新增仓库
     *
     * @param depot 初始信息
     * @return depotId
     */
    String generate(MaterialDepot depot, String operator);

    /**
     * 修改货架
     * @param depot         仓库实体
     * @param operator      操作员
     * @return boolean
     */
    boolean updateDepot(MaterialDepot depot, String operator);

    /**
     * 修改货架
     * @param depotId       仓库Id
     * @param operator      操作员
     * @return boolean
     */
    boolean deleteDepot(String depotId, String operator);


    /**
     * 转移货架
     *
     * @param oldId  原有仓库Id
     * @param newId  目标仓库Id
     * @param rackId 货架Id
     * @return boolean
     */
    boolean transferRack(String oldId, String newId, String rackId, String operator);



    /**
     * 单值查询
     *
     * @param depotId 仓库Id
     * @return vo
     */
    DepotVo queryOne(String depotId);

    /**
     * 查询所包含货架
     *
     * @param depotId depotId
     * @return list
     */
    List<RackVo> queryRacks(String depotId);

    /**
     * 多值分页查询
     *
     * @param size 页大小
     * @param page 页序号
     * @param arg1 条件参数一
     * @param arg2 条件参数二
     * @return list
     */
    PageObject<DepotVo> queryList(Integer size, Integer page, String arg1, String arg2);

    /**
     * 查询所有
     * @return list
     */
    List<DepotVo> queryAll();

    /**
     * 容量检测
     * 检测当前容量是否超过最大值
     * @param vo vo
     * @return 当检测到临近最大容量或容量超过时,返回true
     */
    boolean capacityMaxCheck(DepotVo vo);

    /**
     * 设置仓库容量阈值
     * @param depotId   仓库ID
     * @param threshold 阈值
     * @return boolean
     */
    boolean updateThreshold(String depotId, Long threshold, String operator);



}
