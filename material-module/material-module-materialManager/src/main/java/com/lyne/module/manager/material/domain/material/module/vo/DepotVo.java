package com.lyne.module.manager.material.domain.material.module.vo;

import cn.hutool.core.util.NumberUtil;
import com.lyne.common.core.types.MaterialDepot;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 领域实体
 * 仓库
 *
 * @author lyne
 */
@Document("depot_vo")
@AllArgsConstructor
@NoArgsConstructor
public class DepotVo implements Serializable {
    /**
     * 仓库ID
     */
    @Id
    private String depotId;
    /**
     * 仓库系统名
     */
    private String depotName;
    /**
     * 中文名
     */
    private String depotNameZh;
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
     * 仓库位置
     */
    private String place;
    /**
     * 描述
     */
    private String description;

    /**
     * 当前状态
     */
    private Integer status = 0;
    /**
     * 所属货架
     */
    private List<String> rackList;

    private String createBy;

    private String updateBy;

    private String createDate;
    private String updateDate;
    private String remark;


    public DepotVo(String depotId, String depotName,
                   Long maxCapacity, String place,
                   String description) {
        this.depotId = depotId;
        this.depotName = depotName;
        this.depotNameZh = depotName;
        this.maxCapacity = maxCapacity;
        this.place = place;
        this.description = description;
    }

    public DepotVo(String depotId, String depotName, String depotNameZh,
                   Long maxCapacity, String place, String description) {
        this.depotId = depotId;
        this.depotName = depotName;
        this.depotNameZh = depotNameZh;
        this.maxCapacity = maxCapacity;
        this.place = place;
        this.description = description;
    }


    public DepotVo(MaterialDepot depot) {
        if (depot != null) {
            setDepotId(depot.getDepotId());
            setDepotName(depot.getDepotName());
            setDepotNameZh(depot.getDepotNameZh());
            setDescription(depot.getDescription());
            setPlace(depot.getPlace());
            setMaxCapacity(depot.getMaxCapacity());
            setCapacity(depot.getCapacity());
            setThreshold(depot.getThreshold());
            setDefThreshold();
            setCreateBy(depot.getCreateBy());
            setUpdateBy(depot.getUpdateBy());
            setRemark(depot.getRemark());
        }
    }

    /**
     * 移除货架
     * @param rackId rackId
     */
    public void removeRack(String rackId) {
        List<String> rackList = getRackList();
        rackList.remove(rackId);
        setRackList(rackList);
    }

    /**
     * 添加货架
     * @param rackId rackId
     */
    public void addRack(String rackId) {
        List<String> rackList = getRackList();
        rackList.add(rackId);
        setRackList(rackList);
    }


    /**
     * 设置默认阈值
     */
    public void setDefThreshold() {
        if (getThreshold() == 0 && getMaxCapacity() > 0) {
            long threshold = NumberUtil.parseLong(getMaxCapacity() * 0.95 + "");
            setThreshold(threshold);
        }
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public String getDepotName() {
        if (depotName == null) {
            depotName = "";
        }
        return depotName;
    }

    public void setDepotName(String depotName) {
        if (depotName == null) {
            depotName = "";
        }
        this.depotName = depotName;
        if (depotNameZh == null) {
            setDepotNameZh(depotName);
        }
    }

    public String getDepotNameZh() {
        return depotNameZh;
    }

    public void setDepotNameZh(String depotNameZh) {
        if (depotNameZh == null || "".equals(depotNameZh)) {
            setDepotNameZh(getDepotName());
        }
        this.depotNameZh = depotNameZh;
    }

    public Long getMaxCapacity() {
        if (maxCapacity == null) {
            setMaxCapacity(0L);
        }
        return maxCapacity;
    }

    public void setMaxCapacity(Long maxCapacity) {
        if (maxCapacity == null) {
            return;
        }
        this.maxCapacity = maxCapacity;
    }

    public String getPlace() {
        if (place == null) {
            setPlace("");
        }
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDescription() {
        if (description == null) {
            setDescription("");
        }
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

    public Long getCapacity() {
        if (maxCapacity == null) {
            setMaxCapacity(0L);
        }
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getThreshold() {
        if (threshold == null || threshold <= 0) {
            setThreshold(0L);
        }
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public List<String> getRackList() {
        if (rackList == null) {
            rackList = new ArrayList<>();
        }
        return rackList;
    }

    public void addList(String rackId) {
        if (rackId != null) {
            List<String> rackList = getRackList();
            rackList.add(rackId);
            setRackList(rackList);
        }
    }

    public void setRackList(List<String> rackList) {
        if (rackList == null) {
            return;
        }
        this.rackList = rackList;
    }

    public String getCreateBy() {
        if (this.createBy == null || this.createBy.length() == 0) {
            this.createBy = "system";
        }
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy =  createBy == null || createBy.isEmpty() ? "system" : createBy;
    }

    public String getUpdateBy() {
        if (this.updateBy == null || this.updateBy.length() == 0) {
            this.updateBy = "system";
        }
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy =  updateBy == null || updateBy.isEmpty() ? "system" : updateBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "DepotVo{" +
                "depotId=" + getDepotId() +
                ", depotName='" + getDepotName() + '\'' +
                ", depotNameZh='" + getDepotNameZh() + '\'' +
                ", maxCapacity=" + getMaxCapacity() +
                ", place='" + getPlace() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", createBy='" + getCreateBy() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
