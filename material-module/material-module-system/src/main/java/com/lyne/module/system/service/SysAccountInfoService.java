package com.lyne.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.SysAccountInfo;

import java.util.Map;

/**
 * 用户详情服务接口
 * @author lyne
 */
public interface SysAccountInfoService extends IService<SysAccountInfo> {

    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    PageObject<SysAccountInfo> queryByPage(int page, int size);

    /**
     * 新增用户详情
     * @param accountInfo info
     * @return boolean
     */
    boolean insert(SysAccountInfo accountInfo, String operator);

    /**
     * 修改用户详情
     * @param type 操作方式 update/delete
     * @param info info
     * @return boolean
     */
    boolean updateByType(String type, SysAccountInfo info, String operator);

    // 查询用户信息成功
    Map<String,Object> queryInfo(String accountId);
}
