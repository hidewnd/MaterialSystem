package com.lyne.auth.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.lyne.auth.domain.LoginMsg;
import com.lyne.auth.service.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author lyne
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Value("${security.oauth2.client.access-token-uri:}")
    private String url;

    @Override
    public LoginMsg login(String username, String password, String grantType, String clientId, String clientSecret) {
        HttpRequest post = HttpUtil.createPost(url);
        post.basicAuth(clientId, clientSecret);
        post.body("username=" + username + "&password=" + password + "&grant_type=" + grantType + "&scope=all");
        HttpResponse execute = post.execute();
        JSON parse = JSONUtil.parse(execute.body());
        String accessToken = parse.getByPath("access_token", String.class);
        String refreshToken = parse.getByPath("refresh_token", String.class);
        if (accessToken == null) {
            return new LoginMsg(HttpStatus.HTTP_BAD_REQUEST);
        }
        return new LoginMsg(HttpStatus.HTTP_OK, username, accessToken, refreshToken);
    }

    @Override
    public LoginMsg refresh(String grantType, String refreshToken, String clientId, String clientSecret) {
        HttpRequest post = HttpUtil.createPost(url);
        post.basicAuth(clientId, clientSecret);
        post.body("refresh_token=" + refreshToken + "&grant_type=" + grantType + "&scope=all");
        JSON parse = JSONUtil.parse(post.execute().body());
        String accessToken = parse.getByPath("access_token", String.class);
        String newRefresh = parse.getByPath("refresh_token", String.class);
        String username = parse.getByPath("username", String.class);
        if (accessToken == null) {
            return new LoginMsg(HttpStatus.HTTP_BAD_REQUEST);
        }
        return new LoginMsg(HttpStatus.HTTP_OK, username, accessToken, newRefresh);
    }
}
