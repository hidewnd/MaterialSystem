package com.lyne.module.manager.material.domain.record.repository;

import com.lyne.common.core.types.Registration;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;

/**
 * 持久化接口
 * 记录存储服务
 *
 * @author lyne
 */
public interface RecordRepository {

    /**
     * 数据据更新对象
     * @param vo vo
     */
    void save(RecordVo vo);

    /**
     * 保存出入库记录中物料信息
     * @param vo vo
     */
    void saveRelation(RecordVo vo);

    /**
     * 添加记录 返回实体
     * @param vo vo
     * @return recordId
     */
    String insertSync(Registration vo);

    /**
     * 异步添加记录
     * @param vo vo
     */
    void insert(Registration vo);

    /**
     * 删除对象
     * @param vo vo
     */
    void delete(RecordVo vo);

    /**
     * 计算记录中物料总价
     * @param vo vo
     * @return prices
     */
    Double getTotalValue(RecordVo vo);
}
