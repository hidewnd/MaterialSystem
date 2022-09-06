package com.lyne.auth.controller;

import com.lyne.auth.domain.LoginMsg;
import com.lyne.auth.service.TokenService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.utils.data.DataUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import javax.annotation.Resource;

/**
 * token控制
 *
 * @author lyne
 */
@RestController
@RequestMapping("/token")
public class TokenController {

    @Resource
    private TokenService tokenService;

    @ApiOperation("用户退出登录")
    @PostMapping("/logout")
    public R<?> logout(@RequestHeader(SecurityConstants.AUTHORIZATION_HEADER) String authorization) {
        DataUtil.checkNulls(authorization);
        return tokenService.TokenLogOut(authorization) ? R.okMsg("用户登出系统成功") : R.okMsg("用户登出系统失败");
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody LoginMsg loginMsg) {
        DataUtil.checkNulls(loginMsg.getUsername(), loginMsg.getPassword());
        Map<String, String> res = tokenService.tokenLogin(loginMsg.getUsername(), loginMsg.getPassword());
        return res == null || res.isEmpty() ? R.fail("用户登录失败") : R.ok("用户登录成功", res);
    }

    @ApiOperation("用户刷新token")
    @PostMapping("/refresh")
    public R<Map<String, String>> refresh(@RequestBody String refreshToken) {
        DataUtil.checkNulls(refreshToken);
        Map<String, String> res = tokenService.tokenRefresh(refreshToken);
        return res == null || res.isEmpty() ? R.fail("用户刷新token失败") : R.ok("用户刷新token成功", res);
    }

    @ApiOperation("获取oss签名")
    @PostMapping("/signature")
    public R<?> getOssSignature() {
        Map<String, Object> signature = tokenService.getOssSignature();
        return signature == null || signature.isEmpty() ? R.fail("获取oss签名失败") : R.ok("获取oss签名成功", signature);
    }

}
