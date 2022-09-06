package com.lyne.module.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.base.BaseEntity;
import com.lyne.common.core.base.BaseException;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.constant.CacheConstant;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.SysAccount;
import com.lyne.common.core.types.SysAccountInfo;
import com.lyne.common.core.utils.PageUtil;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.system.exception.SystemServerException;
import com.lyne.module.system.mapper.SysAccountMapper;
import com.lyne.module.system.mapper.SysRoleMapper;
import com.lyne.module.system.service.SysAccountInfoService;
import com.lyne.module.system.service.SysAccountService;
import com.lyne.module.system.utils.RetrieveDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * @author lyne
 */
@Service
public class SysAccountServiceImpl extends ServiceImpl<SysAccountMapper, SysAccount> implements SysAccountService {
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private SysAccountInfoService accountInfoService;
    @Resource
    private SysAccountMapper accountMapper;
    @Resource
    private RedisService redisService;
    //==============
    //    查询操作
    //===============

    @Override
    public SysAccount queryById(String accountId) {
        SysAccount account = getById(accountId);
        if (account == null) {
            return null;
        }
        account.setRoles(sysRoleMapper.selectByAccountId(accountId));
        return account;
    }

    @Override
    public SysAccount queryByName(String username) {
        SysAccount account = accountMapper.selectOneByName(username);
        if (account == null) {
            return null;
        }
        account.setRoles(sysRoleMapper.selectByAccountId(account.getAccountId()));
        return account;
    }

    @Override
    public List<SysAccount> queryList() {
        return queryList(new QueryWrapper<>());
    }

    /**
     * 条件查询用户列表
     * @param wrapper   条件
     * @return list
     */
    @Override
    public List<SysAccount> queryList(Wrapper<SysAccount> wrapper) {
        List<SysAccount> list = list(wrapper);
        for (SysAccount account : list) {
            account.setRoles(sysRoleMapper.selectByAccountId(account.getAccountId()));
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
    public PageObject<SysAccount> queryByPage(int page, int size) {
        PageObject<SysAccount> pageObject = new PageObject<>();
        IPage<SysAccount> iPage = accountMapper.selectByPage(PageUtil.instancePage(page, size));
        if (iPage != null) {
            pageObject.setPageObject(iPage);
            List<SysAccount> element = pageObject.getElement();
            if (element != null && element.size() > 0) {
                for (SysAccount account : element) {
                    account.setRoles(sysRoleMapper.selectByAccountId(account.getAccountId()));
                }
            }
            pageObject.setElement(element);
        }
        return pageObject;
    }

    //==============
    //    更新操作
    //===============

    /**
     * 用户注册，用户详情初始化 insert
     *
     * @param username 用户名
     * @param password 密码
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String register(String username, String password, String operator) {
        String userNameFlag = "username";
        int passwordLengthFlag = 18;
        SysAccount sysAccount = new SysAccount();
        sysAccount.setUsername(username);
        sysAccount.setPassword(password);
        sysAccount.setCreateBy(operator);
        sysAccount.setUpdateBy(operator);
        if (getOne(new QueryWrapper<SysAccount>().eq(userNameFlag, username)) != null) {
            throw new SystemServerException("用户名已被注册");
        }
        if (password.length() > passwordLengthFlag) {
            throw new SystemServerException("密码太长,最长长度应不大于" + passwordLengthFlag);
        }
        boolean save = save(sysAccount);
        if (!save) {
            throw new SystemServerException("注册失败");
        }
        if (!accountInfoService.save(new SysAccountInfo(sysAccount.getAccountId()))) {
            throw new SystemServerException("注册失败");
        }
        return sysAccount.getAccountId();
    }

    /**
     * 用户更新操作
     *
     * @param type      操作类型：update\delete
     * @param account   更新数据
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean updateByType(String type, SysAccount account, String operator) {
        if (StringUtil.hasText(type) && account != null) {
            if (account.getAccountId() != null || StringUtil.hasText(account.getUsername())) {
                account.setUpdateBy(StrUtil.isBlank(operator) ? "system" : operator);
                QueryWrapper<SysAccount> wrapper = new QueryWrapper<>();
                wrapper = account.getAccountId() != null ? wrapper.eq("accountId", account.getAccountId())
                        : wrapper.eq("username", account.getUsername());
                return updateByType(type, account, wrapper);
            }
        }
        return false;
    }

    public boolean updateByType(String type, SysAccount account, QueryWrapper<SysAccount> wrapper) {
        switch (type) {
            case "update":
                return update(account, wrapper);
            case "delete":
                boolean remove = remove(wrapper);
                if (remove) {
                    redisService.setCacheObject(CacheConstant.BLACK_LIST + account.getUsername(), "remove User");
                    redisService.deleteObject(CacheConstant.ACCESS_PRE + account.getUsername());
                    redisService.deleteObject(CacheConstant.REFRESH_PRE + account.getUsername());
                }
                return remove;
            default:
                throw new ArgumentException("参数错误");
        }
    }

    //==============
    //    用户-角色关系操作
    //===============

    /**
     * 添加 <用户-角色> 绑定
     *
     * @param accountId 用户ID
     * @param roleId    角色ID
     * @param operator  操作员
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean bindRole(String accountId, String roleId, @Nullable String operator) {
        DataUtil.checkNumbers(accountId, roleId);
        try {
            return accountMapper.bindRole(accountId, roleId, StrUtil.isBlank(operator) ? "system" : operator) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean bindRoleByList(String accountId, List<String> list, String operator) {
        DataUtil.checkNumbers(accountId);
        if (list != null && list.size() > 0) {
            for (String menuId : list) {
                if (!bindRole(accountId, menuId, operator)) {
                    throw new BaseException("用户绑定角色失败");
                }
            }
        }
        return true;
    }


    /**
     * 移除 <用户-角色> 绑定
     *
     * @param accountId 用户ID
     * @param roleId    角色ID
     * @param operator  操作员
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean removeBind(String accountId, String roleId, @Nullable String operator) {
        DataUtil.checkNumbers(accountId, roleId);
        try {
            return accountMapper.removeRole(accountId, roleId, StrUtil.isBlank(operator) ? "system" : operator) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean removeBindByList(String accountId, List<String> list, String operator) {
        DataUtil.checkNumbers(accountId);
        if (list != null && list.size() > 0) {
            for (String menuId : list) {
                if (!removeBind(accountId, menuId, operator)) {
                    throw new BaseException("用户取消绑定角色失败");
                }
            }
        }
        return true;
    }

    /**
     * 设置<用户-角色> 绑定状态
     *
     * @param accountId 用户ID
     * @param roleId    角色ID
     * @param status    绑定状态
     * @param operator  操作员
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void setBindState(String accountId, String roleId, BaseEntity.Status status, @Nullable String operator) {
        int res = accountMapper.setBindStatus(accountId, roleId, status, StrUtil.isBlank(operator) ? "system" : operator);
        if (res < 0) {
            throw new SystemServerException("设置绑定状态失败");
        }
    }


    /**
     * 找回密码
     * 1. 用户点击找回密码
     * 2. 用户输入绑定的邮箱
     * 3. 检索匹配用户名和邮箱的账号， 发送邮箱验证码
     * 4. 校验验证码
     * 5. 设置新密码
     * @param dto 找回密码数据实体
     * @return 密码
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String retrievePassword(RetrieveDto dto) {
        return null;
    }

    /**
     * 重置用户密码
     * @param accountId 用户Id
     * @param newPassword 新密码。可无
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean resortPassword(String accountId, String newPassword, String operator) {
        DataUtil.checkNumbers(accountId);
        SysAccount account = getById(accountId);
        Assert.notNull(account, "未找到该用户");
        account.setUpdateBy(operator);
        account.setPassword(StringUtil.hasText(newPassword) ? newPassword : "a123456");
        return updateById(account);
    }
}
