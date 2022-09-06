package com.lyne.module.manager.material.infrastructure.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordData {
    @ExcelProperty("出入库编号")
    private String recordId;

    @ExcelProperty("出入库类型")
    private String sign;
    /**
     * 记录总价
     */
    @ExcelProperty("记录总价")
    private Double value;
    @ExcelProperty("当前状态")
    private String status;
    /**
     * 记录总数
     */
    @ExcelProperty("记录总数")
    private Long number;
    @ExcelProperty("创建者")
    private String createBy;
    @ExcelProperty("修改者")
    private String updateBy;

    public RecordData(EnterRecordVo enterRecordVo) {
        if (enterRecordVo != null) {
            this.recordId = enterRecordVo.getRecordId();
            this.sign = "入库记录";
            this.value = enterRecordVo.getValue();
            this.number = enterRecordVo.getNumber();
            this.createBy = enterRecordVo.getCreateBy();
            this.status = enterRecordVo.getRecordStatus();
            this.updateBy = enterRecordVo.getUpdateBy();
        }
    }

    public RecordData(OuterRecordVo outerRecordVo) {
        if (outerRecordVo != null) {
            this.recordId = outerRecordVo.getRecordId();
            this.sign = "出库记录";
            this.value = outerRecordVo.getValue();
            this.number = outerRecordVo.getNumber();
            this.status = outerRecordVo.getRecordStatus();
            this.createBy = outerRecordVo.getCreateBy();
            this.updateBy = outerRecordVo.getUpdateBy();
        }
    }
}
