package com.lyne.common.core.types;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lyne.common.core.base.BaseEntity;

import java.io.Serializable;

/**
 * 值对象
 * 系统权限
 *
 * @author lyne
 */

@TableName("sys_menu")
public class SysMenu extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("menuId")
    private String menuId;        // 权限ID
    @TableField("parentId")
    private String parentId;      // 父级ID

    private String name;        // 权限名
    private String url;         // 请求路径
    private String perms;       // 权限符

    private Integer status;     // 权限状态
    @TableField("delFlag")
    private Integer delFlag;    // 是否删除
    @TableField(exist = false)
    private SysMenu parent;     // 父级

    public SysMenu() {
    }

    public SysMenu(String parentId, String name, String url, String perms) {
        this.parentId = parentId;
        this.name = name;
        this.url = url;
        this.perms = perms;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPerms() {
        return perms;
    }

    public void setPerms(String perms) {
        this.perms = perms;
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

    public SysMenu getParent() {
        return parent;
    }

    public void setParent(SysMenu parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "SysMenu{" +
                "menuId=" + menuId +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", perms='" + perms + '\'' +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", parent=" + parent +
                super.toString() +
                '}';
    }
}
