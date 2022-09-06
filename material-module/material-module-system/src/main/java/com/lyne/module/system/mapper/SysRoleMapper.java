package com.lyne.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.types.SysRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色
 * @author lyne
 */
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 通过用户ID查询角色
     * @param accountId     用户ID
     * @return list
     */
    List<SysRole> selectByAccountId(String accountId);

    /**
     * 角色绑定权限
     * @param roleId        角色Id
     * @param menuId        权限ID
     * @param operator      操作者
     * @return 影响行
     */
    int bindMenu(@Param("roleId") String roleId, @Param("menuId") String menuId, @Param("operator") String operator);

    /**
     * 角色解除绑定权限
     * @param roleId        角色Id
     * @param menuId        权限ID
     * @param operator      操作者
     * @return 影响行
     */
    int removeMenu(@Param("roleId") String roleId, @Param("menuId") String menuId, @Param("operator") String operator);

    /**
     * 角色设置绑定权限状态
     * @param roleId        角色Id
     * @param menuId        权限ID
     * @param operator      操作者
     * @param status        状态值
     * @return 影响行
     */
    int setBindStatus(@Param("roleId") String roleId, @Param("menuId") String menuId,
                      @Param("status") BaseEntity.Status status, @Param("operator") String operator);

    /**
     * 分页查询
     * @param page      page
     * @return iPage
     */
    IPage<SysRole> selectByPage(Page<Object> page);
}
