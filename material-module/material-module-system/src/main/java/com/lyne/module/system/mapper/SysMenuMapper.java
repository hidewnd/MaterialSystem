package com.lyne.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyne.common.core.types.SysMenu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限
 *
 * @author lyne
 */
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    /**
     * 通过角色ID查询
     * @param roleId    角色ID
     * @return list
     */
    List<SysMenu> selectByRoleId(String roleId);

    /**
     * 分页查询
     * @param page      page
     * @return iPage
     */
    IPage<SysMenu> selectByPage(Page<Object> page);
}
