package com.lyne.module.manager.material.domain.material.module.aggregate;

import cn.hutool.core.convert.Convert;
import cn.hutool.crypto.symmetric.AES;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Locale;

/**
 * 聚合根
 * 物料详情
 *
 * @author lyne
 */
@Data
@Document("material_info")
@AllArgsConstructor
@NoArgsConstructor
public class MaterialInfo implements Serializable {
    /**
     * id
     */
    @Id
    private Long id;
    /**
     * 物料ID
     */
    private String materialId;
    /**
     * 入库记录ID
     */
    private String enterRecordId;
    /**
     * 仓库ID
     */
    private String depotId;
    /**
     * 货架ID
     */
    private String rackId;
    /**
     * 物料数量
     */
    private Long number;
    /**
     * 物料单价
     */
    private Double value;
    /**
     * 容量占比
     */
    private Integer capacityRatio;
    /**
     * 物料位置
     */
    private String location;
    /**
     * 物料图像数据
     */
    private String avatar;
    /**
     * 物料二维码
     */
    private String qrCode;
    /**
     * 当前状态
     */
    private Integer status;

    private String depotName;
    private String materialName;
    private String materialNameZh;

    public MaterialInfo(Material material, String rackId) {
        this.rackId = rackId;
        this.enterRecordId = material.getRecordId();
        this.materialId = material.getMaterialId();
        this.number = material.getStock();
        this.value = material.getValue();
        this.capacityRatio = material.getCapacityRatio() == null ? 1 : material.getCapacityRatio();
        this.status = 0;
        setId();
    }

    public MaterialInfo getInstance(MaterialVo materialVo, MaterialRack rack) {
        this.materialId = materialVo.getMaterialId();
        this.rackId = rack.getRackId();
        this.depotId = rack.getDepotId();
        this.capacityRatio = materialVo.getCapacityRatio();
        setId();
        return this;
    }

    public void setLocation(String name, String place) {
        this.location = String.format(Locale.CHINA, "物料位于%s仓库%s号货架， 仓库地址：%s。",
                name, getRackId(), place);
    }

    private static final AES aes = new AES("CBC", "PKCS7Padding",
            // 密钥
            "0123456789ABHAEQ".getBytes(),
            // iv加盐
            "DYgjCEIMVrj2W9xN".getBytes());

    /**
     * 获取二维码数据
     *              0               1          2          3
     * 格式： qr[enterRecordId]-[materialId]-[rackId]-[depotId]
     *
     * @return 生成物料二维码
     */
    public String generateQR() {
        // 原始数据格式
        String data = "qr" + this.enterRecordId + "-" + this.materialId + "-" + this.rackId + "-" + this.depotId;
        // aes加密
        this.qrCode = aes.encryptHex(data);
        return this.qrCode;
    }

    public String generateQR(Long enterRecordId) {
        DataUtil.checkNumbers(enterRecordId);
        // 原始数据格式
        String data = "qr" + enterRecordId + "-" + this.materialId + "-" + this.rackId + "-" + this.depotId;
        // aes加密
        this.qrCode = aes.encryptHex(data);
        return this.qrCode;
    }

    /**
     * 解析二维码数据
     *              0               1          2          3
     * 格式： qr[enterRecordId]-[materialId]-[rackId]-[depotId]
     * @param qrcode 二维码数据
     */
    public static String[] decodeQR(String qrcode) throws RuntimeException {
        String data = aes.decryptStr(qrcode);
        if (StringUtil.isEmpty(data)) {
            throw new ArgumentException("二维码数据为空");
        }
        String[] split = data.substring(2).split("-");
        if (!data.startsWith("qr") || split.length != 4) {
            throw new ArgumentException("无效的物料二维码");
        }
        return split;
    }

    public void addMum(long num) {
        if (num <= 0) {
            return;
        }
        this.number += num;
    }


    public void cutMum(long num) {
        if (this.number == null || this.number - num < 0) {
            throw new ArgumentException("库存不足");
        }
        if (num <= 0) {
            return;
        }
        this.number -= num;
    }


    public Long getId() {
        return id;
    }

    public void setId() {
        setId(getRackId(), getMaterialId());
    }

    public void setId(String rackId, String materialId) {
        this.id = generalId(rackId, materialId);
    }

    // 生成复合id
    public static Long generalId(String s1, String s2) {
        int length = s2.length();
        long id1 = (Convert.toLong(s1.substring(0, 10))
                + Convert.toLong(s2.substring(0, 10))) / 2
                + Convert.toLong(s2.substring(length - 1, length));
        long id2 = (Convert.toLong(s1.substring(10))
                + Convert.toLong(s2.substring(10))) / 2
                + Convert.toLong(s2.substring(length - 1, length));
        return Convert.toLong(id1 + id2);
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getEnterRecordId() {
        return enterRecordId == null ? "1" : enterRecordId;
    }

    public void setEnterRecordId(String enterRecordId) {
        this.enterRecordId = enterRecordId;
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    public Long getNumber() {
        if (number == null) {
            setNumber(0L);
        }
        return number;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Double getValue() {
        if (value == null) {
            setValue(0.0);
        }
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getCapacityRatio() {
        if (this.capacityRatio == null) {
            this.capacityRatio = 1;
        }
        return this.capacityRatio;
    }

    public String getQrCode() {
        if (this.qrCode == null) {
            this.qrCode = generateQR();
        }
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setCapacityRatio(Integer capacityRatio) {
        this.capacityRatio = capacityRatio == null ? 1 : capacityRatio;
    }



    @Override
    public String toString() {
        return "MaterialInfo{" +
                "id=" + id +
                ", materialId=" + materialId +
                ", enterRecordId=" + enterRecordId +
                ", depotId=" + depotId +
                ", rackId=" + rackId +
                ", number=" + number +
                ", value=" + value +
                ", capacityRatio=" + capacityRatio +
                ", location='" + location + '\'' +
                ", avatar='" + avatar + '\'' +
                ", status=" + status +
                '}';
    }
}
