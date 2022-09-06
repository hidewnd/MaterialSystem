package com.lyne.common.core.types;

import cn.hutool.core.util.EnumUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 值对象
 * 物料进出库登记
 *
 * @author lyne
 */
@TableName("mr_registration")
public class Registration extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 记录ID
     */
    @TableId
    private String regId;
    /**
     * 记录标志 0为进库 1为出库
     */
    private Integer regSign = Registration.ENTER;
    /**
     * 数量
     */
    private Long number;
    /**
     * 进库价值
     */
    private Double value;
    /**
     * 记录状态 | 未完成 / 已完成
     */
    private Integer status;
    /**
     * 逻辑删除
     */
    private Integer delFlag;

    public Registration() {
    }

    public Registration(Sign regSign, long number, double value) {
        this.regSign = regSign.value;
        this.number = number;
        this.value = value;
    }

    public static Registration getInstance(Sign status) {
        Registration registration = new Registration();
        registration.setSign(status);
        return registration;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public Integer getRegSign() {
        return regSign;
    }

    public void setRegSign(Integer regSign) {
        this.regSign = regSign;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setSign(Sign regSign) {
        this.regSign = regSign.value;
    }


    public Long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public Double getValue() {
        return value;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }


    @Override
    public String toString() {
        return "Registration{" +
                "regId=" + regId +
                ", regSign=" + regSign +
                ", number=" + number +
                ", value=" + value +
                ", status=" + status +
                ", delFlag=" + delFlag +
                '}';
    }

    /**
     * 类型枚举
     */
    public static final int ENTER = 0;
    public static final int OUTER = 1;

    /**
     * 类型枚举
     */
    public enum Sign {
        // 入库记录 标识
        ENTER(0),
        // 出库记录 标识
        OUT(1);
        public final int value;

        public int getValue() {
            return value;
        }

        Sign(Integer value) {
            this.value = value;
        }
    }

    /**
     * 状态枚举
     */
    public enum Status {
        // 记录初始化
        INITIAL(0, "初始化"),

        // 记录实体工作状态
        Review(1, "审核中"),
        WORKED(2, "记录中/分配中"),
        WORKED_SECOND(3, "入库中/出库中"),

        // 记录完成
        DONE(4, "已完成"),

        // 记录删除 5
        DELETE(-1, "已删除"),
        // 记录关闭 6
        CLOSE(-2, "已关闭"),
        // 记录失败 效果等同CLOSE 7
        FAILED(-3, "审核失败"),
        ;

        private final String msg;
        private final Integer status;

        public String getMsg() {
            return "记录" + msg;
        }

        public static String getMsg(int i) {
            List<Object> msg = EnumUtil.getFieldValues(Status.class, "msg");
            if (i < 0) {
                i = 4 - i;
            }
            return String.valueOf(msg.get(i));
        }

        public Integer getStatus() {
            return status;
        }

        Status(int i, String msg) {
            this.msg = msg;
            this.status = i;
        }
    }
}
