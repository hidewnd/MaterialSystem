package com.lyne.module.manager.material.domain.record.module.aggregate;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 聚合根
 * 出库记录
 * 物料数据 map{materialId: {rackId:num}}
 * @author lyne
 */
@Document("outer_reg")
@EqualsAndHashCode(callSuper = true)
public class OuterRecordVo extends RecordVo implements Serializable {

    /**
     * 记录ID
     */
    @Id
    private String recordId;
    /**
     * 记录总价
     */
    private Double value;
    /**
     * 记录总数
     */
    private Long number;
    /**
     * 出库凭证
     */
    private String proof;
    /**
     * 加密密钥
     */
    private String sm2key;
    /**
     * 待取物料
     * map{materialId: {qrcode:num}}
     */
    private HashMap<String, HashMap<String, Long>> materialMap;

    /**
     * 申请物料信息
     */
    private Map<String, Long> materialNumberMap;

    public OuterRecordVo() {
        setSign(1);
        setVoStatus(Registration.Status.INITIAL);
        initMaterialList();
    }

    public OuterRecordVo(String recordId, Double value, Long number, String proof) {
        setSign(1);
        setVoStatus(Registration.Status.INITIAL);
        initMaterialList();
        this.recordId = recordId;
        this.value = value;
        this.number = number;
        this.proof = proof;
    }

    public static OuterRecordVo getOuter(RecordVo vo) {
        if (vo instanceof OuterRecordVo) {
            return (OuterRecordVo) vo;
        }
        throw new ArgumentException("未查询到该记录或记录类型不正确");
    }

    /*
        自身业务方法
     */

    /**
     * 初始化物料数据映射
     */
    private void initMaterialList() {
        this.materialMap = new HashMap<>(12);
    }

    /**
     * 修改数量映射映射
     * @param materialId  物料Id
     * @param qrCode      物料详情二维码
     * @param number      对应待取数
     */
    public void updateRackMap(String materialId, String qrCode, long number) {
        HashMap<String, Long> rackMap = getMaterialMap().get(materialId);
        if (rackMap == null) {
            rackMap = new HashMap<>();
        }
        rackMap.put(qrCode, number);
        updateMaterialMap(materialId, rackMap);
    }

    /**
     * 更新货架映射
     * @param rackMap map
     */
    public void updateMaterialMap(String materialId, HashMap<String, Long> rackMap) {
        HashMap<String, HashMap<String, Long>> materialMap = getMaterialMap();
        materialMap.put(materialId, rackMap);
        setMaterialMap(materialMap);
    }


    /**
     * 获取记录状态
     * @return string
     */
    @Override
    public String getRecordStatus() {
        int status = this.getStatus();
        if (status >= 1 && status <= 3) {
            return OuterRecordVo.Status.getMsg(status);
        }
        return Registration.Status.getMsg(status);
    }

    /**
     * 获取记录中物料状态
     * @param materialId 物料ID
     */
    public String getMaterialStatus(String materialId) {
        // 已完成/已关闭记录 直接退出
        if (Objects.equals(getStatus(), Registration.Status.CLOSE.getStatus())) {
            return Registration.Status.CLOSE.getMsg();
        }
        HashMap<String, HashMap<String, Long>> materialMap = this.getMaterialMap();
        HashMap<String, Long> rackMap = materialMap.get(materialId);
        if (materialMap.size() == 0 || rackMap == null) {
            return "未记录";
        }
        if (rackMap.size() == 0) {
            return "已出库";
        }
        return "待出库";
    }

    /**
     * 获取指定物料总数
     * @param materialId 物料Id
     * @return number
     */
    public Long getMaterialTotalNumber(String materialId) {
        DataUtil.checkNulls(materialId);
        HashMap<String, Long> rackMap = getMaterialMap().get(materialId);
        AtomicLong total = new AtomicLong();
        if (rackMap != null && !rackMap.isEmpty()) {
            rackMap.forEach((qrCode, num) -> total.addAndGet(num));
        }
        return total.get();
    }

    /**
     * 获取物料对应总数
     * @return number
     */
    public Map<String, Long> getMaterialTotalNumber() {
        Map<String, Long> map = new HashMap<>(8);
        getMaterialMap().forEach((materialId, rackMap) -> {
            AtomicLong total = new AtomicLong();
            if (rackMap != null && !rackMap.isEmpty()) {
                rackMap.forEach((qrCode, num) -> total.addAndGet(num));
            }
            map.put(materialId, total.get());
        });
        return map;
    }


    /**
     * 减少待取物料队列物料对应数量
     * 直接修改记录，未做真实库存检查,谨慎使用
     * @param materialId 物料ID
     * @param qrCode     物料二维码
     * @param number     物料数量
     */
    public void deductMatNumber(String materialId, String qrCode, Long number) {
        // 已完成/已关闭记录 直接退出
        checkFinish();
        DataUtil.checkNulls(materialId, number);
        HashMap<String, Long> rackMap = getMaterialMap().get(materialId);
        if (rackMap == null || rackMap.isEmpty()) {
            throw new ArgumentException("未记录该物料[" + materialId + "]");
        }
        // 预取值
        Long num = rackMap.get(qrCode);
        if (num == null || num < number) {
            throw new ArgumentException("未记录该批次物料或出库数量超出待取值");
        }
        num -= number;
        updateRackMap(materialId, qrCode, num);
    }

    /**
     * 添加物料对应数量
     * 直接修改记录，未做库存检查,谨慎使用
     * @param materialId 物料ID
     * @param qrCode     物料二维码
     * @param number     物料数量
     */
    public void addMatNumber(String materialId, String qrCode, Long number) {
        checkFinish();
        DataUtil.checkNulls(materialId, number);
        HashMap<String, Long> rackMap = getMaterialMap().get(materialId);
        if (rackMap == null || rackMap.isEmpty()) {
            throw new ArgumentException("未记录该物料[" + materialId + "]");
        }
        // 预取值
        Long num = rackMap.get(qrCode);
        if (num == null) {
            throw new ArgumentException("未记录该批次物料");
        }
        num += number;
        updateRackMap(materialId, qrCode, num);
    }


    /**
     * 添加申请物料信息
     * @param materialId    物料ID
     * @param number        物料数量
     */
    public void addMaterial(String materialId, Long number) {
        DataUtil.checkNumbers(materialId, number);
        if (this.materialNumberMap == null) {
            this.materialNumberMap = new HashMap<>(8);
        }
        this.materialNumberMap.merge(materialId, number, Long::sum);
    }

    /**
     * 修改申请物料信息
     * @param materialId    物料ID
     * @param number        物料数量
     */
    public void updateMaterial(String materialId, Long number) {
        DataUtil.checkNumbers(materialId, number);
        if (this.materialNumberMap == null) {
            this.materialNumberMap = new HashMap<>(8);
        }
        this.materialNumberMap.put(materialId, number);
    }

    /**
     * 该记录是否结束
     */
    private void checkFinish() {
        if (this.status < Registration.Status.INITIAL.getStatus()
                || this.status >= Registration.Status.DONE.getStatus()) {
            throw new ArgumentException("该记录已结束");
        }
    }

    /**
     * 添加物料至待取序列
     * @param info 物料详情
     */
    public void addMaterialInfo(MaterialInfo info, Long number) {
        DataUtil.checkNulls(info, number);
        String materialId = info.getMaterialId();
        String rackId = info.getRackId();
        DataUtil.checkNulls(materialId, rackId);
        HashMap<String, Long> qrcodeMap = getMaterialMap().get(materialId);
        if (qrcodeMap == null) {
            qrcodeMap = new HashMap<>(8);
        }
        Long num = qrcodeMap.get(info.getQrCode());
        if (num == null) {
            num = number;
        } else {
            num += number;
        }
        qrcodeMap.put(info.getQrCode(), num);
        updateMaterialMap(materialId, qrcodeMap);
    }


    /**
     * 状态枚举
     */
    public enum Status {
        // 记录初始化
        Review(1, "审核中"),
        Distribution(2, "分配中"),
        Worked(3, "出库中"),
        ;
        private final String msg;
        private final Integer status;

        public String getMsg() {
            return "记录" + msg;
        }

        public static String getMsg(int i) {
            List<Object> msg = EnumUtil.getFieldValues(OuterRecordVo.Status.class, "msg");
            return String.valueOf(msg.get(i - 1));
        }

        public Integer getStatus() {
            return status;
        }

        Status(int i, String msg) {
            this.msg = msg;
            this.status = i;
        }
    }

    /*
        getter and setter
     */

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
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
        this.value = NumberUtil.round(value, 2).doubleValue();
    }

    public Long getNumber() {
        if (number == null) {
            setNumber(0L);
        }
        return number;
    }

    public void setNumber(Long number) {
        if (number == null) {
            return;
        }
        this.number = number;
    }

    public void addNumber(Long number) {
        Long num = getNumber();
        setNumber(num + number);
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }


    public HashMap<String, HashMap<String, Long>> getMaterialMap() {
        if (this.materialMap == null) {
            this.materialMap = new HashMap<>();
        }
        return this.materialMap;
    }

    public void setMaterialMap(HashMap<String, HashMap<String, Long>> materialMap) {
        this.materialMap = materialMap;
    }

    public String getSm2key() {
        return sm2key;
    }

    public void setSm2key(String sm2key) {
        this.sm2key = sm2key;
    }

    public Map<String, Long> getMaterialNumberMap() {
        if (this.materialNumberMap == null) {
            this.materialNumberMap = new HashMap<>(8);
        }
        return materialNumberMap;
    }

    public void setMaterialNumberMap(Map<String, Long> materialNumberMap) {
        this.materialNumberMap = materialNumberMap == null ? new HashMap<>(2) : materialNumberMap;
    }

    @Override
    public String toString() {
        return "OuterRecordVo{" +
                super.toString() +
                "recordId=" + getRecordId() +
                ", value=" + getValue() +
                ", number=" + getNumber() +
                ", proof='" + getProof() + '\'' +
                ", sm2key='" + getSm2key() + '\'' +
                ", materialMap=" + getMaterialMap() +
                ", materialNumberMap=" + getMaterialNumberMap() +
                '}';
    }
}
