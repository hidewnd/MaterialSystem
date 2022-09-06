package com.lyne.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.SysAccount;
import com.lyne.common.core.types.SysAccountInfo;
import com.lyne.common.core.types.SysMenu;
import com.lyne.common.core.types.SysRole;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.system.service.SysAccountInfoService;
import com.lyne.module.system.service.SysAccountService;
import com.lyne.module.system.service.SysMenuService;
import com.lyne.module.system.service.SysRoleService;
import com.lyne.module.system.utils.VerifyUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 数据查询接口
 *
 * @author lyne
 */
@Slf4j
@RestController
@RequestMapping("/query")
@CacheConfig(cacheNames = "query", cacheManager = "redisCacheManager")
public class QueryController {

    @Resource
    private SysAccountService accountService;
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysMenuService menuService;
    @Resource
    private SysAccountInfoService accountInfoService;

    @ApiOperation("单数据查询")
    @GetMapping("/one/{name}/{type}/{values}")
    public R<?> queryOne(@PathVariable @ApiParam("表名") String name,
                         @PathVariable @ApiParam("方式") String type,
                         @PathVariable @ApiParam("条件值") String values) {
        DataUtil.checkNulls(name, type, values);
        switch (name) {
            case "account":
                return queryAccountOne(type, values);
            case "info":
                return queryAccountInfoOne(type, values);
            case "role":
                return queryRoleOne(type, values);
            case "menu":
                return queryMenuOne(type, values);
            default:
                throw new ArgumentException("参数错误");
        }
    }


    @ApiOperation("多数据查询")
    @GetMapping(value = {"/list/{name}/{type}",
            "/list/{name}/{type}/{column}/{arg1}",
            "/list/{name}/{type}/{column}/{arg1}/{arg2}"})
    public R<?> queryList(@PathVariable("name") @ApiParam("表名") String name,
                          @PathVariable("type") @ApiParam("方式") String type,
                          @PathVariable(value = "column", required = false) @ApiParam("字段名") String column,
                          @PathVariable(value = "arg1", required = false) @ApiParam("参数一") String variable1,
                          @PathVariable(value = "arg2", required = false) @ApiParam("参数二") String variable2) {
        DataUtil.checkNulls(name, type);
        switch (name) {
            case "account":
                Wrapper<SysAccount> accountWrapper = queryList(type, column, variable1, variable2, SysAccount.class);
                return R.ok("查询用户列表成功", accountService.queryList(accountWrapper));
            case "role":
                Wrapper<SysRole> roleWrapper = queryList(type, column, variable1, variable2, SysRole.class);
                return R.ok("查询角色列表成功", roleService.selectList(roleWrapper));
            case "menu":
                Wrapper<SysMenu> menuWrapper = queryList(type, column, variable1, variable2, SysMenu.class);
                return R.ok("查询权限列表成功", menuService.list(menuWrapper));
            default:
                throw new ArgumentException("未知数据类型");
        }
    }

    @ApiOperation("多数据分页查询")
    @PostMapping("/page")
//    @Cacheable(key = "'query:page:'+#name+':'+#page+':'+#size", unless = "#result==null")
    public R<?> queryListByPage(@ApiParam("数据类型") @RequestParam("name") String name,
                                @ApiParam(value = "页码") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam(value = "分页大小") @RequestParam(value = "size", required = false) Integer size) {
        DataUtil.checkNulls(name);
        page = page == null ? 0 : page;
        size = size == null ? 0 : size;
        switch (name) {
            case "account":
                return R.ok("分页查询用户列表成功", accountService.queryByPage(page, size));
            case "role":
                return R.ok("分页查询角色列表成功", roleService.queryByPage(page, size));
            case "menu":
                return R.ok("分页查询权限列表成功", menuService.queryByPage(page, size));
            default:
                R.fail("未知数据类型");
        }
        return R.fail("未知数据类型");
    }

    @PostMapping("/info")
    public R<?> queryAccountInfo(@RequestHeader(SecurityConstants.TOKEN_USER_ID) String accountId) {
        DataUtil.checkNulls(accountId);
        return R.ok("查询成功", accountInfoService.queryInfo(accountId));
    }

    private R<SysMenu> queryMenuOne(String type, String values) {
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        switch (type) {
            case "id":
                wrapper.eq("menuId", values);
                break;
            case "perms":
                wrapper.eq("perms", values);
                break;
            case "name":
                wrapper.eq("name", values);
                break;
            default:
                throw new ArgumentException("参数异常");
        }
        return R.ok("查询权限数据成功", menuService.getOne(wrapper));
    }

    private R<SysRole> queryRoleOne(String type, String values) {
        switch (type) {
            case "id":
                return R.ok("查询角色数据成功", roleService.selectById(values));
            case "name":
                return R.ok("查询角色数据成功", roleService.selectByName(values));
            default:
                throw new ArgumentException("参数异常");
        }
    }

    private R<SysAccount> queryAccountOne(String type, String values) {
        switch (type) {
            case "id":
                return R.ok("查询用户数据成功", accountService.queryById(values));
            case "name":
                return R.ok("查询用户数据成功", accountService.queryByName(values));
            default:
                throw new ArgumentException("参数异常");
        }
    }

    private R<?> queryAccountInfoOne(String type, String values) {
        QueryWrapper<SysAccountInfo> wrapper = new QueryWrapper<>();
        if ("id".equals(type)) {
            wrapper.eq("accountId", values);
        } else {
            throw new ArgumentException("参数异常");
        }
        return R.ok("查询用户详情数据成功", accountInfoService.getOne(wrapper));
    }

    private <T> Wrapper<T> queryList(String type, String column, String variable1, String variable2, Class<?> clazz) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if ("all".equals(type)) {
            return wrapper;
        }
        DataUtil.checkNulls(column, variable1);
        VerifyUtil.columnCheck(column, clazz);
        switch (type) {
            case "gt":
                wrapper.gt(column, variable1);
                break;
            case "ge":
                wrapper.ge(column, variable1);
                break;
            case "lt":
                wrapper.lt(column, variable1);
                break;
            case "le":
                wrapper.le(column, variable1);
                break;
            case "bet":
                DataUtil.checkNulls(variable2);
                wrapper.between(column, variable1, variable2);
                break;
            case "like":
                wrapper.like(column, variable1);
                break;
            case "notBet":
                DataUtil.checkNulls(variable2);
                wrapper.notBetween(column, variable1, variable2);
                break;
            default:
                throw new ArgumentException("参数异常");
        }
        return wrapper;
    }

}
