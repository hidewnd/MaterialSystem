package com.lyne.module.manager.material.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * dto
 * 物料类型
 *
 * @author lyne
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeDto {
    /**
     * 物料类型ID
     */
    private String typeId;
    /**
     * 父级类型ID
     */
    private String parentId;
    /**
     * 物料类型名
     */
    private String typeName;
    /**
     *  物料类型中文
     */
    private String typeNameZh;

    private String remark;

    private Integer status;

}
