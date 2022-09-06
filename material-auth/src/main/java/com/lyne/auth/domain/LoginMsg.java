package com.lyne.auth.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lyne
 */
public class LoginMsg {
    private Integer code;
    private String username;
    private String password;
    private String accessToken;
    private String refreshToken;

    public LoginMsg() {
    }

    public LoginMsg(Integer code) {
        this.code = code;
    }

    public LoginMsg(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginMsg(Integer code, String username, String accessToken, String refreshToken) {
        this.code = code;
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getToken() {
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("refresh_token", refreshToken);
        return map;
    }
}
