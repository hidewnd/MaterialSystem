package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;

/**
 * 值对象
 * 物料
 *
 * @author lyne
 */
@TableName("mr_material")
public class Material extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物料ID
     */
    @TableId
    private String materialId;
    /**
     * 物料类型ID
     */
    private String materialTypeId;
    /**
     * 物料名
     */
    private String materialName;
    /**
     * 物料名
     */
    private String materialNameZh;
    /**
     * 价值
     */
    private Double value;
    /**
     * 容量比值
     */
    private Integer capacityRatio;
    /**
     * 物料信息
     */
    private String message;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 逻辑删除符号
     */
    private Integer delFlag;
    /**
     * 数量
     */
    @TableField(exist = false)
    private Long stock;
    /**
     * 货架ID
     */
    @TableField(exist = false)
    private String rackId;
    /**
     * 入库ID
     */
    @TableField(exist = false)
    private String recordId;

    private String remark;


    public Material() {
    }

    public Material(String materialName, Double value, String message) {
        this.materialName = materialName;
        this.materialNameZh = materialName;
        this.value = value;
        this.message = message;
    }

    public Material(String materialName, String materialNameZh, Double value, String message) {
        this.materialName = materialName;
        this.materialNameZh = materialNameZh;
        this.value = value;
        this.message = message;
    }

    public Material(String materialTypeId, String materialName,String materialNameZh, Double value, String message) {
        this.materialTypeId = materialTypeId;
        this.materialName = materialName;
        this.materialNameZh = materialName;
        this.value = value;
        this.message = message;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialNameZh() {
        return materialNameZh;
    }

    public void setMaterialNameZh(String materialNameZh) {
        this.materialNameZh = materialNameZh;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Integer getCapacityRatio() {
        if (capacityRatio == null) {
            setCapacityRatio(1);
        }
        return capacityRatio;
    }

    public void setCapacityRatio(Integer capacityRatio) {
        this.capacityRatio = capacityRatio;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Long getStock() {
        if (stock == null) {
            setStock(0L);
        }
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Material{" +
                "materialId=" + materialId +
                ", materialTypeId=" + materialTypeId +
                ", materialName='" + materialName + '\'' +
                ", materialNameZh='" + materialNameZh + '\'' +
                ", value=" + value +
                ", capacityRatio=" + capacityRatio +
                ", message='" + message + '\'' +
                ", status=" + status +
                ", defFlag=" + delFlag +
                super.toString() +
                '}';
    }
}
