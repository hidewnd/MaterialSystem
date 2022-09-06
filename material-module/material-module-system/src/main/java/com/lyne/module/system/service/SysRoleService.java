package com.lyne.module.system.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.SysRole;

import java.util.List;
import javax.annotation.Nullable;

/**
 * 角色服务接口
 *
 * @author lyne
 */

public interface SysRoleService extends IService<SysRole> {
    /**
     * 通过ID查询
     * @param roleId roleID
     * @return role
     */
    SysRole selectById(String roleId);

    /**
     * 通过角色名查询
     * @param roleName role
     * @return role
     */
    SysRole selectByName(String roleName);

    /**
     * 查询所有角色
     * @return list
     */
    List<SysRole> selectList();

    /**
     * 条件查询角色
     * @param wrapper 条件构造器
     * @return list
     */
    List<SysRole> selectList(Wrapper<SysRole> wrapper);

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<SysRole> queryByPage(int page, int size);

    /**
     * 添加角色
     * @param roleName 角色名
     * @param nameZh 角色中文名
     * @param sort 角色排序
     * @return boolean
     */
    boolean insert(String roleName, String nameZh, Integer sort, String operator);

    /**
     * 角色修改
     * @param type 操作类型 update/delete
     * @param role 角色
     * @return boolean
     */
    boolean updateByType(String type, SysRole role, String operator);

    /**
     * 添加<role-menu>绑定
     *
     * @param roleId   角色ID
     * @param menuId   权限ID
     * @param operator 操作员
     */
    boolean bindMenu(String roleId, String menuId, @Nullable String operator);

    /**
     * 添加<role-menu>绑定
     *
     * @param roleId    角色ID
     * @param list      权限ID集合
     * @param operator  操作员
     */
    boolean bindMenuByList(String roleId, List<String> list, @Nullable String operator);

    /**
     * 移除<role-menu>绑定
     *
     * @param roleId   角色ID
     * @param menuId   权限ID
     * @param operator 操作员
     */
    boolean removeBind(String roleId, String menuId, @Nullable String operator);

    /**
     * 移除<role-menu>绑定
     *
     * @param roleId    角色ID
     * @param list      权限ID集合
     * @param operator  操作员
     */
    boolean removeBindByList(String roleId, List<String> list, @Nullable String operator);

    /**
     * 设置<role-menu>绑定状态
     *
     * @param roleId   角色ID
     * @param menuId   权限ID
     * @param status   绑定状态
     * @param operator 操作员
     */
    void setBindState(String roleId, String menuId, BaseEntity.Status status, @Nullable String operator);
}
