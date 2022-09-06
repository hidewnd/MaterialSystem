package com.lyne.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.SysMenu;

/**
 * 权限服务接口
 * @author lyne
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 添加权限
     * @param parentId      父级权限
     * @param menuName      权限名
     * @param url           权限路径
     * @return boolean
     */
    boolean insert(String parentId, String menuName, String url, String operator);

    /**
     * 权限修改
     * @param type      修改方式 update/delete
     * @param menu      权限
     * @return boolean
     */
    boolean updateByType(String type, SysMenu menu, String operator);

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<SysMenu> queryByPage(int page, int size);
}
