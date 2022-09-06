package com.lyne.module.manager.material;

import com.lyne.common.security.annotation.EnableCloudFeign;
import com.lyne.common.web.annotation.EnableApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 物料管理服务
 *
 * @author lyne
 */
@EnableMongoRepositories
@EnableApplication
@EnableScheduling
@EnableCloudFeign
public class MaterialManager {
    public static void main(String[] args) {
        SpringApplication.run(MaterialManager.class, args);
    }
}
