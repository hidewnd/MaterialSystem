package com.lyne.gateway.filter;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.constant.CacheConstant;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.common.core.utils.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Date;
import javax.annotation.Resource;

/**
 * token检查
 * @author lyne
 */
@Component
public class TokenFilter implements WebFilter {

    @Resource
    private RedisService redisService;
    private String userName;
    private ServerHttpRequest.Builder requestBuild;

    @Override
    @SuppressWarnings(" NullableProblems")
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        requestBuild = request.mutate();
        String path = request.getURI().getPath();
        if (StringUtil.isMatch("/auth/token/**", path)) {
            return chain.filter(exchange);
        }
        String authorization = request.getHeaders().getFirst(SecurityConstants.TOKEN_AUTHENTICATION);
        String jwt = JwtUtils.getJwt(authorization);
        if (StrUtil.isBlank(jwt)) {
            return chain.filter(exchange);
        }
        String public_key = redisService.getCacheObject("public_key");
        Claims claims = JwtUtils.parseJWT(public_key, jwt);
        userName = claims.get(SecurityConstants.TOKEN_USERNAME, String.class);
        Date expiration = claims.getExpiration();
        long between = DateUtil.between(DateUtil.date(), DateUtil.date(expiration), DateUnit.MINUTE);
        if (between <= 10) {
            refresh(exchange);
        }
        String userId = claims.get(SecurityConstants.TOKEN_USER_ID, String.class);
        ServerHttpRequest newRequest = requestBuild
                .header(SecurityConstants.TOKEN_USERNAME, userName)
                .header(SecurityConstants.TOKEN_USER_ID, userId)
                .build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 刷新token
     */
    private void refresh(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String refresh = request.getHeaders().getFirst("refresh");
        //取消重复刷新
        if (StringUtil.hasText(refresh) && refresh.equals("true")) {
            return;
        }
        String accessToken = refresh();
        if (StringUtil.hasText(accessToken)) {
            requestBuild = requestBuild.header("refresh", "true")
                    .header(SecurityConstants.TOKEN_AUTHENTICATION, SecurityConstants.TOKEN_PREFIX + accessToken);
        }
    }

    private String refresh() {
        HttpRequest post = HttpUtil.createPost("http://localhost:7010/token/refresh");
        String refresh = redisService.getCacheObject(CacheConstant.REFRESH_PRE + userName);
        post.body("refresh=" + refresh);
        HttpResponse execute = post.execute();
        JSONObject jsonObject = JSONUtil.parseObj(execute.body());
        if (jsonObject.getByPath("code", Integer.class) == HttpStatus.SUCCESS) {
            return jsonObject.getByPath("data.access_token", String.class);
        }
        return null;
    }


}
