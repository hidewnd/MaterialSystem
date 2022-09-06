package com.lyne.module.system.mapper;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.types.SysAccount;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 用户
 * @author lyne
 */
@Repository
public interface SysAccountMapper extends BaseMapper<SysAccount> {

    /**
     * 绑定角色
     * @param accountId     用户ID
     * @param roleId        角色ID
     * @param operator      操作人
     * @return 影响值
     */
    int bindRole(@Param("accountId") String accountId, @Param("roleId") String roleId, @Param("operator") String operator);

    /**
     * 移除绑定角色
     * @param accountId     用户ID
     * @param roleId        角色ID
     * @param operator      操作人
     * @return 影响值
     */
    int removeRole(@Param("accountId") String accountId, @Param("roleId") String roleId, @Param("operator") String operator);

    /**
     * 设置绑定状态
     * @param accountId     用户ID
     * @param roleId        角色ID
     * @param status        状态值
     * @param operator      操作人
     * @return 影响值
     */
    int setBindStatus(@Param("accountId") String accountId, @Param("roleId") String roleId,
                      @Param("status") BaseEntity.Status status, @Param("operator") String operator);

    /**
     * 分页查询
     * @param page          page
     * @return iPage
     */
    IPage<SysAccount> selectByPage(Page<Object> page);

    SysAccount selectOneByName(String username);
}
