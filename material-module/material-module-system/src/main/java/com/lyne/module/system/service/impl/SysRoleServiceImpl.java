package com.lyne.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.base.BaseException;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.SysRole;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.system.exception.SystemServerException;
import com.lyne.module.system.mapper.SysMenuMapper;
import com.lyne.module.system.mapper.SysRoleMapper;
import com.lyne.module.system.service.SysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * 角色
 *
 * @author lyne
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Resource
    private SysMenuMapper sysMenuMapper;
    @Resource
    private SysRoleMapper roleMapper;

    //==============
    //    查询操作
    //===============

    @Override
    public SysRole selectById(String roleId) {
        SysRole role = getOne(new QueryWrapper<SysRole>().eq("roleId", roleId));
        role.setMenus(sysMenuMapper.selectByRoleId(roleId));
        return role;
    }

    @Override
    public SysRole selectByName(String roleName) {
        SysRole role = getOne(new QueryWrapper<SysRole>().eq("roleName", roleName));
        role.setMenus(sysMenuMapper.selectByRoleId(role.getRoleId()));
        return role;
    }

    @Override
    public List<SysRole> selectList() {
        return selectList(new QueryWrapper<>());
    }

    @Override
    public List<SysRole> selectList(Wrapper<SysRole> wrapper) {
        List<SysRole> list = list(wrapper);
        for (SysRole role : list) {
            role.setMenus(sysMenuMapper.selectByRoleId(role.getRoleId()));
        }
        return list;
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<SysRole> queryByPage(int page, int size) {
        PageObject<SysRole> pageObject = new PageObject<>();
        IPage<SysRole> iPage = roleMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }

    //==============
    //    更新操作
    //===============

    @Override
    public boolean insert(String roleName, String nameZh, Integer sort, String operator) {
        DataUtil.checkNulls(roleName);
        SysRole role = new SysRole(roleName, nameZh, sort != null ? sort : 10);
        role.setUpdateBy(operator);
        role.setCreateBy(operator);
        return save(role);
    }

    @Override
    public boolean updateByType(String type, SysRole role, String operator) {
        if (StringUtil.hasText(type) && role != null) {
            if (role.getRoleId() != null || StringUtil.hasText(role.getRoleName())) {
                QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
                wrapper = role.getRoleId() != null ? wrapper.eq("roleId", role.getRoleId())
                        : wrapper.eq("roleName", role.getRoleName());
                role.setUpdateBy(operator);
                return updateByType(type, role, wrapper);
            }
        }
        return false;
    }

    private boolean updateByType(String type, SysRole role, QueryWrapper<SysRole> wrapper) {
        switch (type) {
            case "update":
                return update(role, wrapper);
            case "delete":
                return remove(wrapper);
            default:
                throw new ArgumentException("参数错误");
        }
    }

    //==============
    //    角色-权限相关操作
    //===============

    /**
     * 添加<role-menu>绑定
     *
     * @param roleId   角色ID
     * @param menuId   权限ID
     * @param operator 操作员
     */
    @Override
    public boolean bindMenu(String roleId, String menuId, @Nullable String operator) {
        DataUtil.checkNumbers(roleId, menuId);
        try {
            return roleMapper.bindMenu(roleId, menuId, StrUtil.isBlank(operator) ? "system" : operator) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindMenuByList(String roleId, List<String> list, @Nullable String operator) {
        DataUtil.checkNumbers(roleId);
        if (list != null && list.size() > 0) {
            for (String menuId : list) {
                if (!bindMenu(roleId, menuId, operator)) {
                    throw new BaseException("角色绑定权限失败");
                }
            }
        }
        return true;
    }

    /**
     * 移除<role-menu>绑定
     *
     * @param roleId   角色ID
     * @param menuId   权限ID
     * @param operator 操作员
     */
    @Override
    public boolean removeBind(String roleId, String menuId, @Nullable String operator) {
        DataUtil.checkNumbers(roleId, menuId);
        try {
            return roleMapper.removeMenu(roleId, menuId, StrUtil.isBlank(operator) ? "system" : operator) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBindByList(String roleId, List<String> list, @Nullable String operator) {
        DataUtil.checkNumbers(roleId);
        if (list != null && list.size() > 0) {
            for (String menuId : list) {
                if (!removeBind(roleId, menuId, operator)) {
                    throw new BaseException("角色取消绑定权限失败");
                }
            }
        }
        return true;
    }

    /**
     * 设置<role-menu>绑定状态
     *
     * @param roleId   角色ID
     * @param menuId   权限ID
     * @param status   绑定状态
     * @param operator 操作员
     */
    @Override
    public void setBindState(String roleId, String menuId, BaseEntity.Status status, @Nullable String operator) {
        DataUtil.checkNumbers(roleId, menuId);
        DataUtil.checkNulls(status);
        int res = roleMapper.setBindStatus(roleId, menuId, status, StrUtil.isBlank(operator) ? "system" : operator);
        if (res < 0) {
            throw new SystemServerException("绑定失败");
        }
    }
}
