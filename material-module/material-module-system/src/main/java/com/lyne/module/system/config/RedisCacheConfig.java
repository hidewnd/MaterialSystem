package com.lyne.module.system.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 *
 * @author lyne
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {
    /**
     * 过期时间
     */
    @Value("${spring.redis.lettuce.shutdown-timeout}")
    private Integer timeout;
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        //fastjson官方提供了redis序列化类
        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();
        //配置序列化(解决乱码的问题)
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //设置 key 过期时间
                .entryTtl(Duration.ofSeconds(timeout))
                //设置value序列化规则
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(genericFastJsonRedisSerializer))
                .disableCachingNullValues();
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
