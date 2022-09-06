package com.lyne.common.core.base;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 对象基类
 *
 * @author lyne
 */
public class BaseEntity implements Serializable {
    /**
     * 创建者
     */
    @TableField(value = "createBy", fill = FieldFill.INSERT)
    private String createBy;
    /**
     * 创建时间
     */
    @TableField(value = "createDate", fill = FieldFill.INSERT)
    private Date createDate;
    /**
     * 更新者
     */
    @TableField(value = "updateBy", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    /**
     * 更新时间
     */
    @TableField(value = "updateDate", fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
    /**
     * 备注
     */
    private String remark;
    public BaseEntity() {
    }

    public BaseEntity(String createBy, String updateBy, String remark) {
        this.createBy = createBy;
        this.updateBy = updateBy;
        this.remark = remark;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = StrUtil.isBlank(createBy) ? "system" : createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = StrUtil.isBlank(updateBy) ? "system" : updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return ", createBy='" + createBy + '\'' +
                ", createDate=" + createDate +
                ", updateBy='" + updateBy + '\'' +
                ", updateDate=" + updateDate +
                ", remark='" + remark + '\'';
    }


    public enum Status {
        OPEN(0),    // 启用
        CLOSE(1);   // 关闭
        private Integer value;
        private static final Map<Integer, Status> map = new HashMap<>();
        static {
            for (Status status : Status.values()) {
                map.put(status.getValue(), status);
            }
        }
        Status(int status) {
            this.value = status;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
        public static Status getEnumByValue(int value) {
            return map.get(value);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
