package com.lyne.auth;

import com.lyne.common.security.annotation.EnableCloudFeign;
import com.lyne.common.web.annotation.EnableApplication;
import org.springframework.boot.SpringApplication;

/**
 * 授权验证服务
 *
 * @author lyne
 */
@EnableCloudFeign
@EnableApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
