package com.lyne.module.manager.material.domain.record.module.vo;

import cn.hutool.core.util.StrUtil;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;

import java.util.Objects;

/**
 * 抽象类
 * 记录
 *
 * @author lyne
 */
public abstract class RecordVo {

    /**
     * 类型标识
     */
    protected Integer sign;
    /**
     * 实体状态
     */
    protected Integer status;

    private String createBy;
    private String updateBy;
    private String createDate;
    private String updateDate;
    /**
     * 审批结果
     * 执行申请
     */
    protected Integer executeApproval = NO_DISPOSED;
    /**
     * 审批结果
     * 运行申请
     */
    private Integer applyApproval = NO_DISPOSED;


    public Integer getSign() {
        // 默认为进库记录
        if (sign == null) {
            setSign(Registration.ENTER);
        }
        return sign;
    }

    public void setSign(Integer sign) {
        this.sign = sign;
    }

    public void setVoStatus(Registration.Status status) {
        this.status = status.getStatus();
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status == null ? Registration.Status.INITIAL.getStatus() : status;
    }


    public Integer getExecuteApproval() {
        return executeApproval;
    }

    public void setExecuteApproval(Integer executeApproval) {
        this.executeApproval = executeApproval;
    }

    public Integer getApplyApproval() {
        return applyApproval;
    }

    public void setApplyApproval(Integer applyApproval) {
        this.applyApproval = applyApproval;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * 审批处理
     * @param result        审批结果
     * @param operation     操作类型
     * @param role          角色
     * @param username      操作人
     */
    public boolean setApproval(Integer result, String operation, String role, String username) {
        if (StringUtil.isEmpty(operation)) {
            throw new ArgumentException("空值异常");
        }
        switch (operation) {
            // 申请审批
            case "execute":
                if (!"financial".equals(role) && !"root".equals(role)) {
                    throw new ArgumentException("权限不足");
                }
                // 出入库记录状态检查
                if (Registration.Status.INITIAL.getStatus().equals(this.getStatus())) {
                    this.status = Registration.Status.Review.getStatus();
                }
                if (Registration.Status.DONE.getStatus().equals(this.status)) {
                    throw new ArgumentException("该记录已完成");
                }
                if (this.status >= Registration.Status.WORKED.getStatus()
                        || this.status < Registration.Status.INITIAL.getStatus()
                        && this.status > Registration.Status.FAILED.getStatus()) {
                    throw new ArgumentException("该记录正在执行或已取消");
                }
                // 过滤已进入二级审批的申请
                if (this.applyApproval != null && !NO_DISPOSED.equals(this.applyApproval)) {
                    throw new ArgumentException("该申请无法更改，若需要请联系超级管理员");
                }
                this.executeApproval = result;
                // 审批通过后更新记录状态
                if (REFUSE.equals(this.executeApproval)) {
                    this.status = Registration.Status.FAILED.getStatus();
                }
                if (PASS.equals(this.executeApproval)) {
                    this.status = Registration.Status.WORKED.getStatus();
                }
                this.updateBy = StringUtil.hasText(username) ? username : "system";
                return true;
            // 执行审批
            case "apply":
                if (!"warehouse".equals(role) && !"root".equals(role)) {
                    throw new ArgumentException("权限不足");
                }
                // 出入库记录状态检查
                if (Registration.Status.DONE.getStatus().equals(this.status)) {
                    throw new ArgumentException("该记录已完成");
                }
                if (this.status < Registration.Status.INITIAL.getStatus()
                        && this.status > Registration.Status.FAILED.getStatus()) {
                    throw new ArgumentException("该记录已结束");
                }
                // 过滤未进入一级审批的申请
                if (!Objects.equals(this.executeApproval, PASS)) {
                    throw new ArgumentException("出入库申请尚未审批通过");
                }
                // 已处理申请无法重复审批
                if (!NO_DISPOSED.equals(this.applyApproval)) {
                    throw new ArgumentException("该出入库记录执行申请已审批");
                }
                this.applyApproval = result;
                // 审批通过后更新记录状态
                if (PASS.equals(this.applyApproval)) {
                    if (getSign() == Registration.ENTER) {
                        this.status = EnterRecordVo.Status.Written.getStatus();
                    } else {
                        this.status = OuterRecordVo.Status.Distribution.getStatus();
                    }
                }
                if (REFUSE.equals(this.executeApproval)) {
                    this.status = Registration.Status.FAILED.getStatus();
                }
                this.updateBy = StringUtil.hasText(username) ? username : "system";
                return true;
            default:
                throw new ArgumentException("未知操作类型");
        }
    }

    /**
     * 获取记录状态
     *
     * @return string
     */
    public String getRecordStatus() {
        throw new MaterialManagerException("服务尚未实现");
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = StrUtil.isBlank(updateBy) ? "system" : updateBy;
    }

    public static Integer NO_DISPOSED = 0;
    public static Integer PASS = 1;
    public static Integer REFUSE = 2;

}
