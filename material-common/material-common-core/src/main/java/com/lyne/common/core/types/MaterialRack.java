package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 值对象
 * 货架
 *
 * @author lyne
 */
@TableName("mr_rack")
public class MaterialRack extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 货架ID
     */
    @TableId
    private String rackId;
    /**
     * 所属仓库ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String depotId;
    /**
     *  最大容量
     */
    private Long maxCapacity;
    /**
     * 描述
     */
    private String description;

    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 逻辑删除符
     */
    private Integer delFlag;

    /**
     *  包含物料
     */
    @TableField(exist = false)
    private List<Material> materialList;

    public MaterialRack() {
    }

    public MaterialRack(Long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public MaterialRack(Long maxCapacity, String description) {
        this.maxCapacity = maxCapacity;
        this.description = description;
    }

    public MaterialRack(String depotId, Long maxCapacity, String description) {
        this.depotId = depotId;
        this.maxCapacity = maxCapacity;
        this.description = description;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public Long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }


    @Override
    public String toString() {
        return "MaterialRack{" +
                "rackId=" + rackId +
                ", depotId=" + depotId +
                ", maxCapacity=" + maxCapacity +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", materialList=" + materialList +
                super.toString() +
                '}';
    }
}
