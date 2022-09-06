package com.lyne.auth.config;

import com.lyne.auth.config.component.JwtTokenEnhancer;
import com.lyne.auth.service.SysUserDetailService;
import com.lyne.common.core.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

/**
 * 授权配置
 *
 * @author lyne
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 密码加密工具
     */
    @Resource
    private PasswordEncoder passwordEncoder;
    /**
     * 认证管理
     */
    @Resource
    private AuthenticationManager authenticationManager;
    /**
     * 用户查询服务
     */
    @Resource
    private SysUserDetailService sysUserDetailService;
    /**
     * jwt自定义内容
     */
    @Resource
    private JwtTokenEnhancer jwtTokenEnhancer;
    /**
     * jwt格式token
     */
    @Resource
    @Qualifier("jwtTokenStore")
    private TokenStore tokenStore;
    /**
     * jwt <--> access_token 转换
     */
    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    /**
     * 使用密码模式需要配置
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        //设置JWT增强内容
        TokenEnhancerChain chain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers = new ArrayList<>();
        //自定义的内容
        tokenEnhancers.add(jwtTokenEnhancer);
        //转换方式
        tokenEnhancers.add(jwtAccessTokenConverter);
        chain.setTokenEnhancers(tokenEnhancers);
        //默认的认证管理器
        endpoints.authenticationManager(authenticationManager)
                //用户信息查询服务
                .userDetailsService(sysUserDetailService)
                //token的持久性接口
                .tokenStore(tokenStore)
                //jwt <-->access_token转换
                .accessTokenConverter(jwtAccessTokenConverter)
                .tokenEnhancer(chain);
    }

    /**
     * 常规配置
     * 授权类型: authorizedGrantTypes()
     * password: 密码模式
     * authorization_code: 授权码模式
     * refresh_token: 刷新令牌
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client")
                .secret(passwordEncoder.encode("123"))
                .scopes("all")
                //access_token失效时间 秒
                .accessTokenValiditySeconds(SecurityConstants.ACCESS_TOKEN_VALIDITY)
                //refresh_token过期时间 秒
                .refreshTokenValiditySeconds(SecurityConstants.REFRESH_TOKEN_VALIDITY)
                //自动授权
                .autoApprove(true)
                .authorizedGrantTypes("password", "refresh_token");
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        //单点登录配置
        // 获取密钥必须要设置验证
        security.tokenKeyAccess("isAuthenticated()");
    }

}
