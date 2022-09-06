package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 值对象
 * 物料仓库
 *
 * @author lyne
 */
@TableName("mr_depot")
public class MaterialDepot extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 仓库ID
     */
    @TableId
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
    private Long maxCapacity;
    /**
     * 当前容量
     */
    private Long capacity;
    /**
     * 容量阈值
     */
    private Long threshold;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 逻辑删除符
     */
    private Integer delFlag;
    /**
     * 包含货架
     */
    @TableField(exist = false)
    private List<MaterialRack> rackList;

    public MaterialDepot() {
    }

    public MaterialDepot(String depotName, String depotNameZh, String place, String describe, Long maxCapacity) {
        this.depotName = depotName;
        this.depotNameZh = depotNameZh;
        this.place = place;
        this.description = describe;
        this.maxCapacity = maxCapacity;
    }

    public MaterialDepot(String depotName, String place, String describe, Long maxCapacity) {
        this.depotName = depotName;
        this.depotNameZh = depotName;
        this.place = place;
        this.description = describe;
        this.maxCapacity = maxCapacity;
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public String getDepotNameZh() {
        return depotNameZh;
    }

    public void setDepotNameZh(String depotNameZh) {
        this.depotNameZh = depotNameZh;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Long maxCapacity) {
        this.maxCapacity = maxCapacity;
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

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public List<MaterialRack> getRackList() {
        return rackList;
    }

    public void setRackList(List<MaterialRack> rackList) {
        this.rackList = rackList;
    }

    @Override
    public String toString() {
        return "MaterialDepot{" +
                "depotId=" + depotId +
                ", depotName='" + depotName + '\'' +
                ", depotNameZh='" + depotNameZh + '\'' +
                ", place='" + place + '\'' +
                ", description='" + description + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", capacity=" + capacity +
                ", threshold=" + threshold +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", rackList=" + rackList +
                '}';
    }
}
