package com.lyne.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.SysAccountInfo;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.system.mapper.SysAccountInfoMapper;
import com.lyne.module.system.mapper.SysAccountMapper;
import com.lyne.module.system.mapper.SysRoleMapper;
import com.lyne.module.system.service.SysAccountInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;

/**
 * 用户详情服务
 *
 * @author lyne
 */
@Service
public class SysAccountInfoServiceImpl extends ServiceImpl<SysAccountInfoMapper, SysAccountInfo>
        implements SysAccountInfoService {

    @Resource
    private SysAccountMapper accountMapper;
    @Resource
    private SysAccountInfoMapper infoMapper;
    @Resource
    private SysRoleMapper roleMapper;
    /**
     * 分页查询
     * @param page 页码
     * @param size 页大小
     * @return pageObject
     */
    @Override
    public PageObject<SysAccountInfo> queryByPage(int page, int size) {
        PageObject<SysAccountInfo> pageObject = new PageObject<>();
        IPage<SysAccountInfo> iPage = infoMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
        }
        return pageObject;
    }


    //==============
    //    更新操作
    //===============


    /**
     * 新增用户详情
     * @param accountInfo info
     * @return boolean
     */
    @Override
    public boolean insert(SysAccountInfo accountInfo, String operator) {
        if (accountInfo != null && accountMapper.selectById(accountInfo.getAccountId()) != null) {
            return save(accountInfo);
        }
        return false;
    }

    /**
     * 修改用户详情
     * @param type 操作方式 update/delete
     * @param accountInfo info
     * @return boolean
     */
    @Override
    public boolean updateByType(String type, SysAccountInfo accountInfo, String operator) {
        if (StringUtil.hasText(type) && accountInfo.getAccountId() != null) {
            accountInfo.setUpdateBy(StrUtil.isBlank(operator) ? "system" : operator);
            switch (type) {
                case "update":
                    return updateById(accountInfo);
                case "delete":
                    return removeById(accountInfo.getAccountId());
                default:
                    throw new ArgumentException("参数错误");
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> queryInfo(String accountId) {
        DataUtil.checkNumbers(accountId);
        SysAccountInfo accountInfo = getById(accountId);
        Assert.notNull(accountInfo, "该用户不存在");
        HashMap<String, Object> map = new HashMap<>();
        map.put("info", accountInfo);
        map.put("roles",roleMapper.selectByAccountId(accountId));
        return map;
    }

}
