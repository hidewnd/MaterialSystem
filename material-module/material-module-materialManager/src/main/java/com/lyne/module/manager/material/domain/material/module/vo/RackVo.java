package com.lyne.module.manager.material.domain.material.module.vo;

import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.manager.material.infrastructure.utils.exception.CapacityOutOfBoundException;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 领域实体
 * 货架
 *
 * @author lyne
 */
@Data
@Document("rack_vo")
@AllArgsConstructor
@NoArgsConstructor
public class RackVo implements Serializable {
    /**
     * 货架ID
     */
    @Id
    private String rackId;
    /**
     * 仓库ID
     */
    private String depotId;
    /**
     * 最大容量
     */
    private Long maxCapacity;
    /**
     * 当前容量
     * 当前实际容量
     */
    private Long capacity;
    /**
     * 描述
     */
    private String description;
    /**
     * 当前状态
     */
    private Integer status = BaseEntity.Status.OPEN.getValue();
    /**
     * 库存物料列表
     */
    private List<String> materialList;

    /**
     * 临时队列
     * 待取物料 qrcode-number
     */
    private HashMap<String, Long> temporaryQueue = new HashMap<>(8);

    private String createBy;

    private String updateBy;

    private String createDate;
    private String updateDate;

    private String remark;

    /**
     * 容量检测
     * 当容量不满足时返回false
     * @param capacity 待存容量
     */
    public boolean capacityCheck(Long capacity) {
        if (capacity == null || capacity < 0) {
            return false;
        }
        return (getMaxCapacity() - getCapacity()) - capacity >= 0;
    }

    /**
     * 消耗容量
     * @param capacity 消耗量
     */
    public void useCapacity(Long capacity) {
        DataUtil.checkNulls(capacity);
        long total = getMaxCapacity() - getCapacity();
        if (total <= 0 || (total - capacity) < 0) {
            throw new CapacityOutOfBoundException("货架容量不足");
        }
        this.capacity = getCapacity();
        this.capacity += capacity;
    }

    /**
     * 物料出库后操作
     * 释放货架存储容量
     * @param capacity 添加量
     */
    public void freeCapacity(Long capacity) {
        DataUtil.checkNulls(capacity);
        this.capacity = getCapacity();
        this.maxCapacity = getMaxCapacity();
        if (this.capacity <= 0 || this.capacity - capacity < 0) {
            throw new CapacityOutOfBoundException("容量释放超限");
        }
        this.capacity -= capacity;
    }

    /**
     * 记录货架存入物料
     * 修改实时容量,
     * @param materialId    物料Id
     * @param capacity      存入容量
     * @param ratio         物料容量占比
     * @return 剩余数 默认0
     *         容量不足时存入最大可容纳量, 返回剩余未存入数
     */
    public long stored(String materialId, Long capacity, Integer ratio) {
        DataUtil.checkNulls(materialId, capacity, ratio);
        if (getCapacity() < 0 || getCapacity() >= getMaxCapacity()) {
            return capacity / ratio;
        }
        // 实际存储的容量
        Long actualCapacity = capacityCheck(capacity) ? capacity : (Long) (capacity - capacity % ratio);
        useCapacity(actualCapacity);
        addMaterial(materialId);
        return (capacity - actualCapacity) / ratio;
    }


    /**
     * 添加物料id
     * @param matId matId
     */
    public void addMaterial(String matId) {
        DataUtil.checkNulls(matId);
        if (materialList == null) {
            setMaterialList(new ArrayList<>());
        }
        for (String aLong : materialList) {
            if (Objects.equals(aLong, matId)) {
                return;
            }
        }
        materialList.add(matId);
    }

    /**
     * 移除物料id
     * @param matId matId
     */
    public void removeMaterial(String matId) {
        DataUtil.checkNulls(matId);
        if (materialList == null) {
            return;
        }
        this.materialList.remove(matId);
    }

    /**
     * 添加待取物料
     * @param qrcode        物料二维码
     * @param number        存放数量
     */
    public void addTemporary(String qrcode, Long number) {
        DataUtil.checkNulls(qrcode, number);
        this.temporaryQueue.merge(qrcode, number, Long::sum);
    }

    /**
     * 物料出库处理
     * 更新待取物料
     * @param qrcode        物料二维码
     * @param number        存放数量
     */
    public void removeTemporary(String qrcode, Long number) {
        DataUtil.checkNulls(qrcode, number);
        Long temporaryNumber = getTemporaryQueue().get(qrcode);
        if (temporaryNumber == null || temporaryNumber == 0L) {
            throw new MaterialManagerException("该物料未在出库队列中或已出库完毕");
        }
        if (temporaryNumber < number) {
            throw new ArgumentException("出库数量超过出库队列中记录值");
        }
        temporaryNumber -= number;
        if (temporaryNumber == 0L) {
            this.temporaryQueue.remove(qrcode);
        } else {
            this.temporaryQueue.put(qrcode, temporaryNumber);
        }

    }

    /**
     * 清空待取物料
     * @param qrcode    物料二维码
     */
    public void clearTemporary(String qrcode) {
        if (qrcode == null) {
            temporaryQueue.clear();
        }
        DataUtil.checkNulls(qrcode);
        this.temporaryQueue.put(qrcode, 0L);
    }


    public RackVo(MaterialRack rack) {
        if (rack != null) {
            setRackId(rack.getRackId());
            setDepotId(rack.getDepotId());
            setMaxCapacity(rack.getMaxCapacity());
            setCapacity(getMaxCapacity());
            setDescription(rack.getDescription());
            setCreateBy(rack.getCreateBy());
            setUpdateBy(rack.getUpdateBy());
            setRemark(rack.getRemark());
        }
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
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

    public void setCapacity(Long capacity) {
        if (capacity == null) {
            return;
        }
        this.capacity = capacity;
    }

    public Long getCapacity() {
        if (capacity == null) {
            this.capacity = 0L;
        }
        return capacity;
    }

    public Long getMaxCapacity() {
        if (maxCapacity == null) {
            this.maxCapacity = 0L;
        }
        return maxCapacity;
    }

    public void setMaxCapacity(Long maxCapacity) {
        if (maxCapacity == null) {
            return;
        }
        this.maxCapacity = maxCapacity;
    }

    public List<String> getMaterialList() {
        if (materialList == null) {
            setMaterialList(new ArrayList<>());
        }
        return materialList;
    }

    public void setMaterialList(List<String> materialList) {
        if (materialList == null) {
            return;
        }
        this.materialList = materialList;
    }

    public HashMap<String, Long> getTemporaryQueue() {
        if (temporaryQueue == null) {
            setTemporaryQueue(new HashMap<>(8));
        }
        return temporaryQueue;
    }

    public void setTemporaryQueue(HashMap<String, Long> temporaryQueue) {
        this.temporaryQueue = temporaryQueue;
    }

    public String getCreateBy() {
        if (this.createBy == null || this.createBy.length() == 0) {
            this.createBy = "system";
        }
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null || createBy.isEmpty() ? "system" : createBy;
    }

    public String getUpdateBy() {
        if (this.updateBy == null || this.updateBy.length() == 0) {
            this.updateBy = "system";
        }
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null || updateBy.isEmpty() ? "system" : updateBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Override
    public String toString() {
        return "RackVo{" +
                "rackId=" + getRackId() +
                ", depotId=" + getDepotId() +
                ", max_capacity=" + getMaxCapacity() +
                ", capacity=" + getCapacity() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", materialList=" + getMaterialList() +
                '}';
    }
}
