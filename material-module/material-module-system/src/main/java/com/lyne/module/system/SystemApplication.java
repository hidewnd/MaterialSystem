package com.lyne.module.system;

import com.lyne.common.web.annotation.EnableApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 基础服务
 *
 * @author lyne
 */
@EnableApplication
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.lyne")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
