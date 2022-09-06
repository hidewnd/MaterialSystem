package com.lyne.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.SysMenu;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.common.core.utils.http.PathUtils;
import com.lyne.module.system.mapper.SysMenuMapper;
import com.lyne.module.system.service.SysMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lyne
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Resource
    private SysMenuMapper menuMapper;

    //==============
    //    更新操作
    //===============

    /**
     * 添加权限
     * @param parentId      父级权限
     * @param menuName      权限名
     * @param url           权限路径
     * @return boolean
     */
    @Override
    public boolean insert(String parentId, String menuName, String url, String operator) {
        DataUtil.checkNulls(menuName, url);
        if (StringUtil.hasChineseByReg(menuName)) {
            throw new ArgumentException("参数错误");
        }
        String perm = PathUtils.pathToPerm(url);
        if (parentId == null) {
            SysMenu menu = new SysMenu("1", menuName, url, perm);
            return save(menu);
        } else {
            if (getById(parentId) == null) {
                throw new ArgumentException("父级不存在");
            }
            SysMenu menu = new SysMenu(parentId, menuName, url, perm);
            menu.setCreateBy(operator);
            menu.setUpdateBy(operator);
            return save(menu);
        }
    }

    /**
     * 权限修改
     * @param type      修改方式 update/delete
     * @param menu      权限
     * @return boolean
     */
    @Override
    public boolean updateByType(String type, SysMenu menu, String operator) {
        if (StringUtil.hasText(type) && menu != null) {
            if (menu.getMenuId() != null || StringUtil.hasText(menu.getPerms())) {
                QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
                wrapper = menu.getMenuId() != null ?
                        wrapper.eq("menuId", menu.getMenuId())
                        : wrapper.eq("perms", menu.getPerms());
                if (getById(menu.getParentId()) == null) {
                    throw new ArgumentException("父级ID不存在");
                }
                if (StringUtil.hasChineseByReg(menu.getName(), menu.getPerms())) {
                    throw new ArgumentException("参数错误");
                }
                menu.setPerms(PathUtils.pathToPerm(menu.getUrl()));
                menu.setUpdateBy(operator);
                return updateByType(type, menu, wrapper);
            }
        }
        return false;
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<SysMenu> queryByPage(int page, int size) {
        PageObject<SysMenu> pageObject = new PageObject<>();
        IPage<SysMenu> iPage = menuMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }

    private boolean updateByType(String type, SysMenu menu, QueryWrapper<SysMenu> wrapper) {
        switch (type) {
            case "update":
                return update(menu, wrapper);
            case "delete":
                return remove(wrapper);
            default:
                throw new ArgumentException("参数错误");
        }
    }

}
