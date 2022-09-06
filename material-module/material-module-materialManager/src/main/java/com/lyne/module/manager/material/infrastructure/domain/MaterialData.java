package com.lyne.module.manager.material.infrastructure.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialData {
    /**
     * 物料ID
     */
    @ExcelProperty("物料编号")
    private String materialId;
    /**
     * 物料类型ID
     */
    @ExcelProperty("字符传")
    private String typeId;
    /**
     * 物料名
     */
    @ExcelProperty("物料系统名")
    private String materialName;
    /**
     * 物料名
     */
    @ExcelProperty("物料中文名")
    private String materialNameZh;
    /**
     *  价值
     */
    @ExcelProperty("物料价值")
    private Double value;
    /**
     * 容量占比
     */
    @ExcelProperty("容量占比")
    private Integer capacityRatio;
    /**
     * 物料信息
     */
    @ExcelProperty("物料信息")
    private String message;
    /**
     * 当前状态
     */
    @ExcelProperty("当前状态")
    private Integer status;
    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("创建者")
    private String createBy;
    @ExcelProperty("更新者")
    private String updateBy;
    @ExcelProperty("创建时间")
    private Date createDate;
    @ExcelProperty("修改时间")
    private Date updateDate;
}
