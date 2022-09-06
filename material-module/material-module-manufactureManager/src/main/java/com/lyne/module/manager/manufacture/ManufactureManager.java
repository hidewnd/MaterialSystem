package com.lyne.module.manager.manufacture;

import com.lyne.common.web.annotation.EnableApplication;
import org.springframework.boot.SpringApplication;

/**
 * 生产管理服务
 *
 * @author lyne
 */
@EnableApplication
public class ManufactureManager {
    public static void main(String[] args) {
        SpringApplication.run(ManufactureManager.class, args);
    }
}
