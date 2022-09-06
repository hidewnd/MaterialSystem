package com.lyne.module.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.CacheConstant;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.SysAccount;
import com.lyne.common.core.types.SysAccountInfo;
import com.lyne.common.core.types.SysMenu;
import com.lyne.common.core.types.SysRole;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.system.domain.BindDto;
import com.lyne.module.system.service.SysAccountInfoService;
import com.lyne.module.system.service.SysAccountService;
import com.lyne.module.system.service.SysMenuService;
import com.lyne.module.system.service.SysRoleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 更新接口
 *
 * @author lyne
 */
@RestController
public class UpdateController {
    @Resource
    private SysAccountService accountService;
    @Resource
    private SysAccountInfoService accountInfoService;
    @Resource
    private SysRoleService roleService;
    @Resource
    private SysMenuService menuService;
    @Resource
    private RedisService redisService;

    //==============
    //    Account相关
    //===============

    @ApiOperation("用户注册")
    @PostMapping("/insert/register")
    public R<?> register(@RequestParam("username") @ApiParam("用户名") String username,
                         @RequestParam("password") @ApiParam("密码") String password,
                         @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(username, password);
        return R.ok("注册成功", accountService.register(username, password, operator));
    }


    @ApiOperation("用户更新操作")
    @PostMapping("/update/account")
    public R<?> setAccount(@RequestParam("type") @ApiParam("更新方式") String type,
                           @ApiParam("更新参数") SysAccount account,
                           @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(type, account);
        return accountService.updateByType(type, account, operator) ? R.okMsg("更新成功") : R.fail("更新失败");
    }

    @ApiOperation("重置用户密码")
    @PostMapping("/update/resortPassword")
    public R<?> resortPassword(@RequestParam("accountId") @ApiParam("用户Id") String accountId,
                               @RequestParam(value = "newPwd", required = false) @ApiParam("新密码") String newPassword,
                               @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(accountId);
        return accountService.resortPassword(accountId, newPassword, operator)
                ? R.okMsg("重置用户密码成功") : R.fail("重置用户密码失败");
    }


    //==============
    //    AccountInfo相关
    //===============

    @ApiOperation("用户详情修改")
    @PostMapping("/update/account/info")
    public R<?> setAccountInfo(@RequestParam("type") @ApiParam("更新方式") String type,
                               @ApiParam("更新参数") SysAccountInfo accountInfo,
                               @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(accountInfo.getAccountId());
        return accountInfoService.updateByType(type, accountInfo, operator)
                ? R.okMsg("更新成功") : R.fail("更新失败");
    }

    //==============
    //    Role相关
    //===============

    @ApiOperation("角色添加")
    @PostMapping("/insert/role")
    public R<?> insertRole(@RequestParam("roleName") @ApiParam("角色名") String roleName,
                           @RequestParam(value = "nameZh", required = false) @ApiParam("角色中文名") String nameZh,
                           @RequestParam(value = "roleSort", required = false) @ApiParam("优先级") Integer sort,
                           @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        if (StringUtil.hasChineseByReg(roleName) || sort < 0) {
            throw new ArgumentException("参数错误");
        }
        return roleService.insert(roleName, nameZh, sort, operator) ? R.okMsg("添加成功") : R.fail("添加失败");
    }

    @ApiOperation("角色修改")
    @PostMapping("/update/role")
    public R<?> setRole(@RequestParam("type") @ApiParam("更新方式") String type,
                        @ApiParam("更新参数") SysRole role,
                        @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(type, role);
        if (role.getRoleName() != null && StringUtil.hasChineseByReg(role.getRoleName())
                || role.getRoleSort() != null && role.getRoleSort() < 0) {
            throw new ArgumentException("参数错误");
        }
        return roleService.updateByType(type, role, operator) ? R.okMsg("更新成功") : R.fail("更新失败");
    }

    //==============
    //    Menu相关
    //===============

    @ApiOperation("权限添加")
    @PostMapping("/insert/menu")
    public R<?> insertMenu(@RequestParam(value = "parentId", required = false) @ApiParam("父级") String parentId,
                           @RequestParam(value = "menuName") @ApiParam("权限名") String menuName,
                           @RequestParam(value = "url") @ApiParam("权限范围") String url,
                           @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        if (StringUtil.hasChineseByReg(menuName, url)) {
            throw new ArgumentException("参数错误");
        }
        return menuService.insert(parentId, menuName, url, operator) ? R.okMsg("添加成功") : R.fail("添加失败");
    }

    @ApiOperation("权限修改")
    @PostMapping("/update/menu")
    public R<?> setMeu(@RequestParam("type") @ApiParam("更新方式") String type,
                       @ApiParam("更新参数") SysMenu menu,
                       @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(type, menu);
        if ("update".equals(type)) {
            if (StringUtil.hasChineseByReg(menu.getName(), menu.getPerms(), menu.getUrl())) {
                throw new ArgumentException("参数错误");
            }
        }
        boolean res = menuService.updateByType(type, menu, operator);
        if (res) {
            updateRedis();
        }
        return res ? R.okMsg("更新成功") : R.fail("更新失败");
    }

    //==============
    //    绑定相关
    //===============

    @ApiOperation("角色绑定单个权限")
    @PostMapping("/bind/menu/one")
    public R<?> roleBindMenu(@RequestParam("roleId") @ApiParam("角色ID") String roleId,
                             @RequestParam("menuId") @ApiParam("权限ID") String menuId,
                             @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(roleId, menuId);
        boolean res = roleService.bindMenu(roleId, menuId, operator);
        if (res) {
            updateRedis();
        }
        return res ? R.okMsg("角色绑定权限成功") : R.fail("角色绑定权限失败");
    }

    @ApiOperation("角色绑定多个权限")
    @PostMapping("/bind/menu/list")
    public R<?> roleBindMenuByList(@RequestBody BindDto dto,
                                   @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(dto, dto.getId(), dto.getBindList());
        boolean res = roleService.bindMenuByList(dto.getId(), dto.getBindList(), operator);
        if (res) {
            updateRedis();
        }
        return res
                ? R.okMsg("角色绑定权限成功") : R.fail("角色绑定权限失败");
    }

    @ApiOperation("角色取消绑定单个权限")
    @PostMapping("/removeBind/menu/one")
    public R<?> roleRemoveBindMenu(@RequestParam("roleId") @ApiParam("角色ID") String roleId,
                                   @RequestParam("menuId") @ApiParam("权限ID") String menuId,
                                   @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(roleId, menuId);
        boolean res = roleService.removeBind(roleId, menuId, operator);
        if (res) {
            updateRedis();
        }
        return res ? R.okMsg("角色取消绑定权限成功") : R.fail("角色取消绑定权限失败");
    }

    @ApiOperation("角色取消绑定多个权限")
    @PostMapping("/removeBind/menu/list")
    public R<?> roleRemoveBindMenuByList(@RequestBody BindDto dto,
                                         @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(dto, dto.getId(), dto.getBindList());
        boolean res = roleService.removeBindByList(dto.getId(), dto.getBindList(), operator);
        if (res) {
            updateRedis();
        }
        return res
                ? R.okMsg("角色取消绑定权限成功") : R.fail("角色取消绑定权限失败");
    }

    @ApiOperation("用户绑定单个角色")
    @PostMapping("/bind/role/one")
    public R<?> accountBindRole(@RequestParam("accountId") @ApiParam("角色ID") String accountId,
                                @RequestParam("roleId") @ApiParam("权限ID") String roleId,
                                @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(accountId, roleId);
        return accountService.bindRole(accountId, roleId, operator) ? R.okMsg("用户绑定角色成功") : R.fail("用户绑定角色失败");
    }

    @ApiOperation("用户绑定多个角色")
    @PostMapping("/bind/role/list")
    public R<?> accountBindRoleByList(@RequestBody BindDto dto,
                                      @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(dto, dto.getId(), dto.getBindList());
        return accountService.bindRoleByList(dto.getId(), dto.getBindList(), operator)
                ? R.okMsg("用户绑定角色成功") : R.fail("用户绑定角色失败");
    }

    @ApiOperation("用户取消绑定单个角色")
    @PostMapping("/removeBind/role/one")
    public R<?> accountRemoveBindRole(@RequestParam("accountId") @ApiParam("角色ID") String accountId,
                                      @RequestParam("roleId") @ApiParam("权限ID") String roleId,
                                      @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(accountId, roleId);
        return accountService.removeBind(accountId, roleId, operator) ? R.okMsg("用户绑定角色成功") : R.fail("用户绑定角色失败");
    }

    @ApiOperation("用户取消绑定多个角色")
    @PostMapping("/removeBind/role/list")
    public R<?> accountRemoveBindRoleByList(@RequestBody BindDto dto,
                                            @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(dto, dto.getId(), dto.getBindList());
        return accountService.removeBindByList(dto.getId(), dto.getBindList(), operator)
                ? R.okMsg("用户取消绑定角色成功") : R.fail("用户取消绑定角色失败");
    }


    private void updateRedis() {
        for (SysRole role : roleService.selectList()) {
            List<String> perms = role.getMenus().stream().map(SysMenu::getPerms).collect(Collectors.toList());
            redisService.setCacheObject(CacheConstant.MENU_PRE + role.getRoleName(), JSONObject.toJSONString(perms));
        }
    }
}
