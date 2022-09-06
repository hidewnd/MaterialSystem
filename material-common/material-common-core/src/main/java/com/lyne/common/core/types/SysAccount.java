package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.handler.AESEncryptHandler;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 值对象
 * 系统用户
 *
 * @author lyne
 */
@TableName(value = "sys_account", autoResultMap = true)
@EqualsAndHashCode(callSuper = false)
public class SysAccount extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId("accountId")
    private String accountId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 用户密码
     */
    @TableField(value = "password", typeHandler = AESEncryptHandler.class)
    private String password;

    /**
     * 账号状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否删除
     */
    @TableField("delFlag")
    private Integer delFlag;

    /**
     * 用户角色
     */
    @TableField(exist = false)
    private List<SysRole> roles;

    public SysAccount() {
    }

    public SysAccount(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public List<SysRole> getRoles() {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "SysAccount{" +
                "accountId=" + accountId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", roles=" + roles +
                super.toString() +
                '}';
    }
}
