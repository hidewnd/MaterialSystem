package com.lyne.module.manager.material.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDto {
    /**
     *  物料Id
     */
    private String materialId;
    /**
     * 类型id
     */
    private String typeId;
    /**
     * 物料名
     */
    private String materialName;
    /**
     * 物料名
     */
    private String materialNameZh;
    /**
     * 物料价值
     */
    private Double value;
    /**
     * 容量占比
     */
    private Integer capacityRatio;
    /**
     * 物料详情
     */
    private String message;

    private String remark;
    private Integer status;

    public String getMaterialName() {
        if (materialName == null) {
            setMaterialName("");
        }
        return materialName;
    }

    public String getMaterialNameZh() {
        if (materialNameZh == null) {
            setMaterialNameZh("");
        }
        return materialNameZh;
    }

    public Double getValue() {
        if (value == null) {
            value = 0.0;
        }
        return value;
    }

    public Integer getCapacityRatio() {
        if (this.capacityRatio == null) {
            setCapacityRatio(1);
        }
        return capacityRatio;
    }

    public void setCapacityRatio(Integer capacityRatio) {
        this.capacityRatio = capacityRatio == null || capacityRatio <= 0 ? 1 : capacityRatio;
    }

    public String getMessage() {
        if (message == null) {
            setMessage("");
        }
        return message;
    }

}
