package com.lyne.module.manager.workflow.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * druid配置
 * @author lyne
 */
@Configuration
public class DruidConfig {
    @PostConstruct
    public void setProperties() {
        System.setProperty("druid.mysql.usePingMethod", "false");
    }
}