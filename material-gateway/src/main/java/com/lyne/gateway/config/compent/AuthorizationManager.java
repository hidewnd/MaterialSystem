package com.lyne.gateway.config.compent;

import cn.hutool.core.convert.Convert;
import cn.hutool.jwt.JWTException;
import com.alibaba.fastjson.JSONObject;
import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.base.BaseException;
import com.lyne.common.core.constant.CacheConstant;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.common.core.utils.http.PathUtils;
import com.lyne.common.core.utils.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import javax.annotation.Resource;

/**
 * 鉴权管理器
 *
 * @author lyne
 */
@Slf4j
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Resource
    private RedisService redisService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext context) {
        ServerWebExchange exchange = context.getExchange();
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = exchange.getRequest().getURI().getPath();
        log.info("[{}]: 请求路径: {}", "AuthorizationManager", requestPath);
        //对应跨域的预检请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return Mono.just(new AuthorizationDecision(true));
        }
        //格式校验
        String authorization = request.getHeaders().getFirst(SecurityConstants.TOKEN_AUTHENTICATION);
        if (authorization == null || !authorization.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            throw new BaseException("令牌格式错误, 请重新检查");
        }
        String jwt = JwtUtils.getJwt(authorization);
        String publicKey = redisService.getCacheObject("public_key");
        //判断是否在黑名单中
        Claims claims = JwtUtils.parseJWT(publicKey, jwt);
        String username = claims.get(SecurityConstants.TOKEN_USERNAME, String.class);
        String cache = redisService.getCacheObject(CacheConstant.BLACK_LIST + username);
        // 是否黑名单
        if (StringUtil.hasText(cache) || !hasPerms(claims, PathUtils.getPerms(requestPath))) {
            throw new BaseException("无权限访问");
        }
        cache = redisService.getCacheObject(CacheConstant.REFRESH_PRE + username);
        // 是否登录
        if (StringUtil.isEmpty(cache)) {
            throw new JWTException("令牌失效或已过期, 请重新登录系统");
        }
        return Mono.just(new AuthorizationDecision(true));
    }

    /**
     * 判断角色权限
     * @param claims jwt解析参数
     * @param perm 权限符
     * @return boolean
     */
    private boolean hasPerms(Claims claims, String perm) {
        List<String> roles = Convert.toList(String.class, claims.get(SecurityConstants.TOKEN_AUTHORITIES));
        for (String role : roles) {
            List<String> strings = JSONObject.parseArray(redisService.getCacheObject(
                    CacheConstant.MENU_PRE + role), String.class);
            if (PathUtils.inPerms(perm, strings)) {
                return true;
            }
        }
        return false;
    }


}
