package com.lyne.module.material;

import com.lyne.common.web.annotation.EnableApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 物料CRUD服务
 *
 * @author lyne
 */
@EnableScheduling
@EnableApplication
public class MaterialApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaterialApplication.class, args);
    }
}
