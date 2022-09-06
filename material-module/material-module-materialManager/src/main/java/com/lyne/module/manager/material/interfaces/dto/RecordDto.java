package com.lyne.module.manager.material.interfaces.dto;

import cn.hutool.core.convert.Convert;
import com.lyne.common.core.utils.data.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 记录dto
 *
 * @author lyne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDto {
    private String recordId;
    private Integer sign;
    private String materialId;
    private String number;
    private Double value;
    private String qrCode;
    private String rackId;
    private Map<String,String> outerMaterial;

    public void setRecordId(String recordId) {
        this.recordId = StringUtil.trim(recordId);
    }

    public void setMaterialId(String materialId) {
        this.materialId = StringUtil.trim(materialId);
    }

    public void setNumber(String number) {
        this.number = StringUtil.trim(number);
    }

    public String getRecordId() {
        return recordId;
    }

    public Integer getSign() {
        if (sign == null) {
            return 0;
        }
        return sign;
    }

    public String getMaterialId() {
        return materialId;
    }

    public Long getNumber() {
        return Convert.toLong(number);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        if (value != null && value >= 0) {
            this.value = value;
        }
    }

    public String getRackId() {
        return rackId;
    }

    @Override
    public String toString() {
        return "RecordDto{" +
                "recordId='" + recordId + '\'' +
                ", sign=" + sign +
                ", materialId='" + materialId + '\'' +
                ", number='" + number + '\'' +
                ", value=" + value +
                ", qrCode='" + qrCode + '\'' +
                ", rackId='" + rackId + '\'' +
                ", outerMaterial=" + outerMaterial +
                '}';
    }
}
