package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 值对象
 * 物料类型
 *
 * @author lyne
 */
@TableName("mr_type")
public class MaterialType extends BaseEntity implements Serializable {
    /**
     * 物料类型ID
     */
    @TableId
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
     * 物料类型中文
     */
    private String typeNameZh;

    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 逻辑删除符号
     */
    private Integer delFlag;

    @TableField(exist = false)
    private List<MaterialType> childList;

    public MaterialType() {
    }

    public MaterialType(String parentId, String typeName, String typeNameZh) {
        this.parentId = parentId;
        this.typeName = typeName;
        this.typeNameZh = typeNameZh;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeNameZh() {
        return typeNameZh;
    }

    public void setTypeNameZh(String typeNameZh) {
        this.typeNameZh = typeNameZh;
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

    public List<MaterialType> getChildList() {
        return childList;
    }

    public void setChildList(List<MaterialType> childList) {
        this.childList = childList;
    }

    @Override
    public String toString() {
        return "MaterialType{" +
                "typeId=" + typeId +
                ", parentId='" + parentId + '\'' +
                ", typeName='" + typeName + '\'' +
                ", typeNameZh='" + typeNameZh + '\'' +
                ", status=" + status +
                ", defFlag=" + delFlag +
                super.toString() +
                '}';
    }
}
