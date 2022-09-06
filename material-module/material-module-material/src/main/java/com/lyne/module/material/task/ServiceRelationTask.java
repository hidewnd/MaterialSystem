package com.lyne.module.material.task;

import cn.hutool.core.convert.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 *
 * @author lyne
 */
@Component
public class ServiceRelationTask {

    private static final Logger log = LoggerFactory.getLogger(ServiceRelationTask.class);
    @Value("${spring.redis.host:localhost}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private String port;
    private boolean redisOk = false;

    @Scheduled(cron = "* 0/1 * * * ?")
    public void checkRedis() {
        try {
            if (!redisOk) {
                Jedis jedis = new Jedis(host, Convert.toInt(port));
                String ping = jedis.ping();
                if (ping.equalsIgnoreCase("PONG")) {
                    log.info("[Redis] 连接Redis成功！ 连接地址: {}:{}", host, port);
                    redisOk = true;
                }
            }
        } catch (Exception e) {
            log.error("[Redis] 连接Redis失败！ 连接地址: {}:{}", host, port);
        }
    }
}
