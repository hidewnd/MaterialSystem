package com.lyne.module.system.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.SysAccount;
import com.lyne.module.system.utils.RetrieveDto;

import java.util.List;
import javax.annotation.Nullable;

/**
 * 用户服务接口
 *
 * @author lyne
 */
public interface SysAccountService extends IService<SysAccount> {

    /**
     * 通过ID查询
     * @param accountId 用户ID
     * @return SysAccount
     */
    SysAccount queryById(String accountId);

    /**
     * 通过用户名查询
     * @param username 用户名
     * @return SysAccount
     */
    SysAccount queryByName(String username);

    /**
     * 查询所有用户列表
     * @return list
     */
    List<SysAccount> queryList();

    /**
     * 条件查询用户列表
     * @param wrapper   条件
     * @return list
     */
    List<SysAccount> queryList(Wrapper<SysAccount> wrapper);

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<SysAccount> queryByPage(int page, int size);


    /**
     * 用户注册
     * @param username  用户名
     * @param password  用户密码
     * @return 用户ID
     */
    String register(String username, String password, String operator);

    /**
     * 用户更新操作
     *
     * @param type      操作类型：update\delete
     * @param account   更新数据
     * @return boolean
     */
    boolean updateByType(String type, SysAccount account, String operator);

    /**
     * 添加 <用户-角色> 绑定
     *
     * @param accountId 用户ID
     * @param roleId    角色ID
     * @param operator  操作员
     */
    boolean bindRole(String accountId, String roleId, @Nullable String operator);

    boolean bindRoleByList(String accountId, List<String> list, String operator);

    /**
     * 移除 <用户-角色> 绑定
     *
     * @param accountId 用户ID
     * @param roleId    角色ID
     * @param operator  操作员
     */
    boolean removeBind(String accountId, String roleId, @Nullable String operator);

    boolean removeBindByList(String accountId, List<String> list, String operator);

    /**
     * 设置<用户-角色> 绑定状态
     *
     * @param accountId 用户ID
     * @param roleId    角色ID
     * @param status    绑定状态
     * @param operator  操作员
     */
    void setBindState(String accountId, String roleId, BaseEntity.Status status, @Nullable String operator);

    /**
     * 找回密码
     * @param dto 找回密码数据实体
     * @return 密码
     */
    String retrievePassword(RetrieveDto dto);

    /**
     * 重置用户密码
     * @param accountId 用户Id
     * @param newPassword 新密码。可无
     * @return boolean
     */
    boolean resortPassword(String accountId, String newPassword, String operator);


}
