package com.lyne.auth.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Druid配置
 * 解决druid 日志报错：discard long time none received connection:xxx
 *
 * @author lyne
 */
@Configuration
public class DruidConfig {
    @PostConstruct
    public void setProperties() {
        System.setProperty("druid.mysql.usePingMethod", "false");
    }
}
