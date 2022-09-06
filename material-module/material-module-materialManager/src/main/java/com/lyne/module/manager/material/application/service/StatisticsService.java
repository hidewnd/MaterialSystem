package com.lyne.module.manager.material.application.service;

import java.util.Map;

/**
 * 统计服务
 * @author lyne
 */
public interface StatisticsService {
    /**
     * 获取库存统计
     */
    Map<String, Long> getStatisticsTotal();

    /**
     * 统计库存物料类型占比
     */
    Map<String, Object> getStockTypeTotal();

    /**
     * 统计出入库物料类型占比
     */
    Map<String, Object> getRecordTypeTotal();

    /**
     * 统计出入库总数
     * 入库记录总数
     * 出库记录总数
     */
    Map<String, Object> getRecordTotal();

    /**
     * 统计仓库最大容量
     * 仓库容量阈值
     * 仓库当前容量
     */
    Map<String,Object> getDepotTotal();


    /**
     * 统计物料容量统计
     * 统计出入库占比
     */
    Map<String,Object> getMaterialTotal();

}
