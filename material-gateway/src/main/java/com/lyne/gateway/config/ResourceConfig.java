package com.lyne.gateway.config;

import cn.hutool.core.convert.Convert;
import com.lyne.gateway.config.compent.AuthorizationManager;
import com.lyne.gateway.config.compent.IgnoreUrls;
import com.lyne.gateway.filter.TokenFilter;
import com.lyne.gateway.handler.ReactAuthenticationEntryPoint;
import com.lyne.gateway.handler.RestfulAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 安全配置
 *
 * @author lyne
 */
@EnableWebFluxSecurity
public class ResourceConfig {
    @Autowired
    private IgnoreUrls ignoreUrls;
    @Autowired
    private AuthorizationManager authorizationManager;
    /**
     * 自定义token无效/过期响应
     */
    @Autowired
    private ReactAuthenticationEntryPoint reactAuthenticationEntryPoint;
    /**
     * 自定义token未授权响应
     */
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private TokenFilter tokenFilter;


    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                // 白名单配置
                .pathMatchers(Convert.toStrArray(ignoreUrls.getUrls())).permitAll()
                // 鉴权管理器配置
                .anyExchange().access(authorizationManager)
                .and()
                .addFilterBefore(tokenFilter, SecurityWebFiltersOrder.FIRST)
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(reactAuthenticationEntryPoint)
                .and().csrf().disable();
        return http.build();
    }
}
