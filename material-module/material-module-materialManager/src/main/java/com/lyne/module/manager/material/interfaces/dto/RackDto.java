package com.lyne.module.manager.material.interfaces.dto;

import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RackDto implements Serializable {
    /**
     * 货架ID
     */
    private String rackId;
    /**
     * 所属仓库ID
     */
    private String depotId;
    /**
     * 最大容量
     */
    private String maxCapacity;
    /**
     * 描述
     */
    private String description;

    private String remark;

    private Integer status;


    public Long getMaxCapacity() {
        if (maxCapacity == null) {
            setMaxCapacity("0");
        }
        return Convert.toLong(maxCapacity);
    }

    @Override
    public String toString() {
        return "RackDto{" +
                "rackId='" + rackId + '\'' +
                ", depotId='" + depotId + '\'' +
                ", maxCapacity='" + maxCapacity + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
