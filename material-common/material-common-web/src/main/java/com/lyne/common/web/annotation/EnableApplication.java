package com.lyne.common.web.annotation;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.annotation.AliasFor;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用启动器注解
 * @author lyne
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@EnableOpenApi
@SpringBootApplication
@EnableDiscoveryClient
public @interface EnableApplication {

    /**
     * 排除特定的自动配置类，使它们永远不会被应用
     */
    @AliasFor(annotation = EnableAutoConfiguration.class)
    Class<?>[] exclude() default {};

    /**
     *排除特定的自动配置类名称，使其永远不会应用
     */
    @AliasFor(annotation = EnableAutoConfiguration.class)
    String[] excludeName() default {};


    /**
     * 如果为 true，ServiceRegistry 将自动注册本地服务器。
     */
    @AliasFor(annotation = EnableDiscoveryClient.class)
    boolean autoRegister() default true;
}
