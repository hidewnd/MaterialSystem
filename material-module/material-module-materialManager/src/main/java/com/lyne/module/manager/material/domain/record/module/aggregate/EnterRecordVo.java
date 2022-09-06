package com.lyne.module.manager.material.domain.record.module.aggregate;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;
import com.lyne.module.manager.material.infrastructure.utils.exception.StateCheckException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聚合根
 * 入库记录
 *
 * @author lyne
 */
@Data
@Document("enter_reg")
@EqualsAndHashCode(callSuper = true)
public class EnterRecordVo extends RecordVo {
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
     * 待入库物料
     */
    private List<String> materialList;
    /**
     * 登记物料数
     */
    private Map<String, Long> matNumber;
    /**
     * 待入库物料数
     */
    private Map<String, Long> numberMap;

    public static EnterRecordVo getEnter(RecordVo vo) {
        if (vo instanceof EnterRecordVo) {
            return (EnterRecordVo) vo;
        }
        throw new ArgumentException("未查询到该记录或记录类型不正确");
    }

    /**
     * 获取记录状态
     * @return string
     */
    @Override
    public String getRecordStatus() {
        int status = this.getStatus();
        if (status >= 1 && status <= 3) {
            return EnterRecordVo.Status.getMsg(status);
        }
        return Registration.Status.getMsg(status);
    }


    /**
     * 添加记录数数量
     *
     * @param materialId 物料ID
     * @param number     记录数
     */
    public void addMatNumber(String materialId, Long number, boolean isCheck) {
        // 判断记录状态
        if (isCheck){
            isWorked(null);
        }
        Assert.notNull(materialId, "存在空值： materialId");
        DataUtil.checkNumbers(number);
        Map<String, Long> matNumber = getMatNumber();
        matNumber.merge(materialId, number, Long::sum);
        this.matNumber = matNumber;
        this.numberMap = matNumber;
    }


    /**
     * 添加物料信息
     * @param materialId materialId
     */
    public void addMaterialVoList(String materialId) {
        DataUtil.checkNulls(materialId);
        List<String> materialList = getMaterialList();
        for (String s : materialList) {
            if (s.equals(materialId)) {
                return;
            }
        }
        this.materialList.add(materialId);
    }

    /**
     * status = work
     * 添加物料记录
     * @param materialId    物料ID
     * @param number        记录数
     */
    public void record(String materialId, Long number) {
        // 判断记录状态
        isWorked(null);
        DataUtil.checkNulls(materialId, number);
        addMaterialVoList(materialId);
        addMatNumber(materialId, number, true);
        totalNumber();
    }

    public void constructorRecord(String materialId, Long number) {
        // 判断记录状态
        DataUtil.checkNulls(materialId, number);
        addMaterialVoList(materialId);
        addMatNumber(materialId, number, false);
        totalNumber();
    }

    /**
     * 统计记录总数
     */
    public void totalNumber() {
        long sum = this.getMatNumber().values().stream().mapToLong(l -> l).sum();
        this.setNumber(sum);
    }

    /**
     * 判断该记录是否处于工作状态,若处于工作状态则抛出异常
     * @param status 当为非工作状态，则值不为空时,切换状态
     */
    public void isWorked(EnterRecordVo.Status status) {
        if (Registration.Status.CLOSE.getStatus().equals(this.getStatus())
                || Registration.Status.DONE.getStatus().equals(this.getStatus())
                || Registration.Status.FAILED.getStatus().equals(this.getStatus())) {
            throw new StateCheckException("记录已结束或已完成");
        }
        if (status != null && getStatus() <= status.getStatus()) {
            this.setStatus(status.getStatus());
        }
    }

    /**
     * 入库记录中数据
     * 减少物料对应数
     * @param materialId 物料Id
     * @param num 减少数
     */
    public void reduceMatNumber(String materialId, Long num) {
        Map<String, Long> map = getNumberMap();
        DataUtil.checkNulls(materialId, num);
        Long number = map.get(materialId);
        if (number == null || number <= 0) {
            throw new ArgumentException("值为空");
        }
        if ((number - num) < 0) {
            throw new ArgumentException("输入值超过限制");
        }
        map.put(materialId, number - num);
        setNumberMap(map);
    }

    /**
     * 状态枚举
     */
    public enum Status {
        // 记录初始化
        Review(1, "审核中"),
        Recorded(2, "记录中"),
        Written(3, "入库中"),
        ;
        private final String msg;
        private final Integer status;

        public String getMsg() {
            return "状态" + msg;
        }

        public static String getMsg(int i) {
            List<Object> msg = EnumUtil.getFieldValues(EnterRecordVo.Status.class, "msg");
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

    public EnterRecordVo() {
        setSign(0);
        setStatus(Registration.Status.INITIAL.getStatus());
        setStatus(Registration.Status.INITIAL.getStatus());
        value = 0.0;
        number = 0L;
        this.matNumber = new HashMap<>();
        this.numberMap = new HashMap<>();
        this.materialList = new ArrayList<>();
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

    public List<String> getMaterialList() {
        if (materialList == null) {
            materialList = new ArrayList<>();
        }
        return materialList;
    }

    public Map<String, Long> getMatNumber() {
        if (matNumber == null) {
            this.matNumber = new HashMap<>(8);
        }
        return matNumber;
    }

    public Map<String, Long> getNumberMap() {
        if (numberMap == null) {
            this.numberMap = getMatNumber();
        }
        return numberMap;
    }

    @Override
    public String toString() {
        return "EnterRecordVo{" +
                "recordId=" + getRecordId() +
                ", sign=" + getSign() +
                ", status=" + getStatus() +
                ", value=" + getValue() +
                ", number=" + getNumber() +
                ", materialList=" + getMaterialList() +
                '}';
    }
}
