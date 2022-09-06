package com.lyne.gateway.filter;

import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.utils.http.WebFluxUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

/**
 * 全局拦截器
 * 添加必要数据
 * addHeader(header, HttpHeaders.AUTHORIZATION, "Basic "
 * + Base64.getUrlEncoder().encodeToString(("client:123").getBytes()));
 *
 * @author lyne
 */
@Configuration
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        ServerHttpRequest.Builder mutate = exchange.getRequest().mutate();
        if ("/auth/oauth/token".equals(path)) {
            mutate.header(HttpHeaders.AUTHORIZATION, SecurityConstants.TOKEN_PREFIX
                    + Base64.getUrlEncoder().encodeToString(("client:123").getBytes()));
        }
        mutate.header("X-Forwarded-For", WebFluxUtil.getIpAddress(exchange.getRequest()));

        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
