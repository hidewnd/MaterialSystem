package com.lyne.module.manager.material.domain.material.module.vo;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.exception.ArgumentException;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 领域实体
 * 物料信息
 *
 * @author lyne
 */
@Data
@Document("material_vo")
public class MaterialVo implements Serializable {
    /**
     * 物料ID
     */
    @Id
    private String materialId;
    /**
     * 物料类型ID
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
     *  价值
     */
    private Double value;
    /**
     * 容量占比
     */
    private Integer capacityRatio;
    /**
     * 物料信息
     */
    private String message;
    /**
     * 当前状态
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

    private String createBy;
    private String updateBy;
    private Date createDate;
    private Date updateDate;


    public MaterialVo() {
        this.status = 0;
        setValue(0.0);
    }

    public MaterialVo(String materialId, String typeId, String materialName,
                      String materialNameZh, Double value, String message) {
        this.materialId = materialId;
        this.typeId = typeId;
        this.materialName = materialName;
        this.materialNameZh = materialNameZh;
        setValue(value);
        this.message = message;
        this.status = 0;
        dataCheck();
    }

    /**
     * 获取单价
     *
     * @param total 该物料总价
     * @return double
     */
    public Double getUnitPrice(Double total) {
        BigDecimal round = NumberUtil.round((total / this.getValue()), 2);
        return round.doubleValue();
    }

    /**
     * 获取总价
     *
     * @param number 该物料数量
     * @return double
     */
    public Double getTotalPrice(Long number) {
        BigDecimal round = NumberUtil.round((number * this.getValue()), 2);
        return round.doubleValue();
    }

    /**
     * 校验自身数据
     */
    public void dataCheck() {
        // 必要值检查
        if (StrUtil.hasEmpty(this.materialName)) {
            throw new ArgumentException("参数空值异常");
        }
        // 异常值处理
        if (this.value <= 0.0) {
            this.value = 0.0;
        }

        if (getStatus() == null) {
            this.setStatus(0);
        }
    }

    public Double getValue() {
        if (value == null) {
            setValue(0.0);
        }
        return value;
    }

    public void setValue(Double value) {
        if (value == null) {
            return;
        }
        this.value = value;
    }

    public Integer getCapacityRatio() {
        if (this.capacityRatio == null) {
            setCapacityRatio(1);
        }
        return capacityRatio;
    }

    public void setCapacityRatio(Integer capacityRatio) {
        this.capacityRatio = capacityRatio == null || capacityRatio <= 1 ? 1 : capacityRatio;
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

    public void setStatus(BaseEntity.Status status) {
        this.status = status.getValue();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    @Override
    public String toString() {
        return "MaterialVo{" +
                "materialId=" + getMaterialId() +
                ", typeId=" + getTypeId() +
                ", materialName='" + getMaterialName() + '\'' +
                ", materialNameZh='" + getMaterialNameZh() + '\'' +
                ", value=" + getValue() +
                ", capacityRatio=" + getCapacityRatio() +
                ", message='" + getMessage() + '\'' +
                ", status=" + getStatus() +
                ", remark='" + getRemark() + '\'' +
                '}';
    }
}
