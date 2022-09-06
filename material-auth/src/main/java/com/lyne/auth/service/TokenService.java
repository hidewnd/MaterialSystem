package com.lyne.auth.service;

import java.util.Map;

/**
 * 授权服务
 * @author lyne
 */
public interface TokenService {

    Map<String, String> tokenLogin(String username, String password);

    Map<String, String> tokenRefresh(String refreshToken);

    boolean TokenLogOut(String authorization);

    Map<String, Object> getOssSignature();
}
