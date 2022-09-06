package com.lyne.auth.config;

import com.lyne.auth.config.component.JwtTokenEnhancer;
import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.constant.SecurityConstants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.PostConstruct;
import java.security.KeyPair;

/**
 * JWT相关配置
 *
 * @author lyne
 */
@Configuration
public class JwtConfig {


    @Bean
    public JwtTokenEnhancer jwtTokenEnhancer() {
        return new JwtTokenEnhancer();
    }

    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    @Primary
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair()); // 设置密钥对
        return jwtAccessTokenConverter;
    }

    //从证书中获取密钥对
    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
                new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void initData() {
        String publicKey = Base64.encodeBase64String(keyPair().getPublic().getEncoded());
        redisService.setCacheObject(SecurityConstants.PUBLIC_KEY, publicKey);
    }
}
