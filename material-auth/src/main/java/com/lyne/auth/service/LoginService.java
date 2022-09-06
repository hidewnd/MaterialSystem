package com.lyne.auth.service;

import com.lyne.auth.domain.LoginMsg;

/**
 * 用户登录服务
 *
 * @author lyne
 */
public interface LoginService {

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param grantType 授权方式
     * @param clientId 客户端Id
     * @param clientSecret secret
     * @return msg
     */
    LoginMsg login(String username, String password, String grantType, String clientId, String clientSecret);

    /**
     * 刷新令牌
     * @param refreshToken 刷新token
     * @param grantType 授权方式
     * @param clientId 客户端Id
     * @param clientSecret secret
     * @return msg
     */
    LoginMsg refresh(String grantType, String refreshToken, String clientId, String clientSecret);
}
