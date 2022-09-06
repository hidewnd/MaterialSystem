package com.lyne.common.core.types;

import java.io.Serializable;

/**
 * 复合实体类
 * 仓库中的物料信息
 *
 * @author lyne
 */
public class RegMaterial implements Serializable {
   private String rackId;
    /**
     * 记录ID
     */
    private String registrationId;
    /**
     * 物料ID
     */
    private String materialId;

    /**
     * 进出库标识
     */
    private Integer sign;
    /**
     * 数量
     */
    private Long number;
    /**
     * 物料
     */
    private Material material;

    public RegMaterial() {
    }

    public RegMaterial(String registrationId) {
        this.registrationId = registrationId;
    }

    public RegMaterial(String registrationId, String materialId, int sign, long number) {
        this.registrationId = registrationId;
        this.materialId = materialId;
        this.sign = sign;
        this.number = number;
    }

    public RegMaterial(String registrationId, String materialId, long number) {
        this.registrationId = registrationId;
        this.materialId = materialId;
        this.number = number;
    }



    public RegMaterial(String registrationId, Material material, Long number) {
        this.registrationId = registrationId;
        this.material = material;
        if (material != null) {
            this.materialId = material.getMaterialId();
        }
        setNumber(number);
    }

    public RegMaterial(String registrationId, int sign, String materialId, Long number) {
        this.registrationId = registrationId;
        this.materialId = materialId;
        this.sign = sign;
        setNumber(number);
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getSign() {
        return sign;
    }

    public void setSign(Integer sign) {
        this.sign = sign;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        if (number != null && number <= 0) {
            this.number = 0L;
        } else {
            this.number = number;
        }
    }

    @Override
    public String toString() {
        return "RegMaterial{" +
                "rackId=" + rackId +
                ", registrationId=" + registrationId +
                ", sign=" + sign +
                ", material=" + material +
                ", number=" + number +
                '}';
    }
}
