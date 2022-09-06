package com.lyne.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyne.common.core.types.SysAccountInfo;
import org.springframework.stereotype.Repository;

/**
 * 用户详情
 * @author lyne
 */
@Repository
public interface SysAccountInfoMapper extends BaseMapper<SysAccountInfo> {

    /**
     * 分页查询
     * @param page          page
     * @return iPage
     */
    IPage<SysAccountInfo> selectByPage(Page<Object> page);
}
