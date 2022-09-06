package com.lyne.module.manager.material.interfaces.dto;

import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepotDto {
    /**
     * 仓库ID
     */
    private String depotId;
    /**
     * 仓库系统名
     */
    private String depotName;
    /**
     * 仓库中文名
     */
    private String depotNameZh;
    /**
     * 仓库地址
     */
    private String place;
    /**
     * 描述
     */
    private String description;
    /**
     * 最大容量
     */
    private String maxCapacity;

    private String threshold;

    private String remark;
    private Integer status;

    public Long getMaxCapacity() {
        if (maxCapacity == null) {
            setMaxCapacity("1");
        }
        return Convert.toLong(maxCapacity);
    }

    public Long getThreshold() {
        if (threshold == null) {
            setThreshold("1");
        }
        return Convert.toLong(threshold);
    }
}
