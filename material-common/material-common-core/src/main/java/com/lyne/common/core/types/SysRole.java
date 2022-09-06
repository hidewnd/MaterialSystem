package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * 值对象
 * 系统角色
 *
 * @author lyne
 */
@TableName("sys_role")
public class SysRole extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("roleId")
    private String roleId;            // 角色ID
    @TableField("roleName")
    private String roleName;        // 角色名
    @TableField("roleNameZh")
    private String roleNameZh;      // 角色名（中文）
    @TableField("roleSort")
    private Integer roleSort;        // 角色排序

    private Integer stated;         // 角色状态
    @TableField("delFlag")
    private Integer delFlag;        // 是否删除

    @TableField(exist = false)
    private List<SysMenu> menus;    // 角色权限

    public SysRole() {
    }

    public SysRole(String roleName, String roleNameZh, Integer roleSort) {
        this.roleName = roleName;
        this.roleNameZh = roleNameZh;
        this.roleSort = roleSort;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleNameZh() {
        return roleNameZh;
    }

    public void setRoleNameZh(String roleNameZh) {
        this.roleNameZh = roleNameZh;
    }

    public Integer getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(Integer roleSort) {
        this.roleSort = roleSort;
    }

    public Integer getStated() {
        return stated;
    }

    public void setStated(Integer stated) {
        this.stated = stated;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public List<SysMenu> getMenus() {
        return menus;
    }

    public void setMenus(List<SysMenu> menus) {
        this.menus = menus;
    }

    @Override
    public String toString() {
        return "SysRole{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleNameZh='" + roleNameZh + '\'' +
                ", roleSort='" + roleSort + '\'' +
                ", stated=" + stated +
                ", delFlag=" + delFlag +
                ", menus=" + menus +
                super.toString() +
                '}';
    }
}
