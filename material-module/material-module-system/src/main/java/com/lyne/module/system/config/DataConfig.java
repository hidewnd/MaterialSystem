package com.lyne.module.system.config;

import com.alibaba.fastjson.JSONObject;
import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.constant.CacheConstant;
import com.lyne.common.core.types.SysMenu;
import com.lyne.common.core.types.SysRole;
import com.lyne.common.mq.config.RabbitMqConfig;
import com.lyne.module.system.service.SysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 初始化数据
 *
 * @author lyne
 */

@Configuration
public class DataConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqConfig.class);
    @Resource
    private RedisService redisService;

    @Resource
    private SysRoleService roleService;

    @PostConstruct
    public void initMenu() {
        for (SysRole role : roleService.selectList()) {
            List<String> perms = role.getMenus().stream().map(SysMenu::getPerms).collect(Collectors.toList());
            redisService.setCacheObject(CacheConstant.MENU_PRE + role.getRoleName(), JSONObject.toJSONString(perms));
        }
        log.info("[{}]: 初始化权限缓存成功", getClass().getSimpleName());
    }
}
