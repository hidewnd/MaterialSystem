package com.lyne.module.manager.material.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 * @author lyne
 */
@Slf4j
@EnableAsync
@Configuration
public class ThreadConfig {

    @Value("${thread.pool.corePoolSize:10}")
    private int corePoolSize;

    @Value("${thread.pool.maxPoolSize:20}")
    private int maxPoolSize;

    @Value("${thread.pool.queueCapacity:50}")
    private int queueCapacity;

    @Value("${thread.pool.keepAliveSeconds:60}")
    private int threadPoolKeepAliveSeconds;


    /**
     * '@Primary' 优先使用该全局配置线程池
     * 如果不加@primary @async注解默认采用SimpleAsyncTaskExecutor
     * 不加@primary 可使用@async("threadPoolTaskExecutor")指定线程池
     *
     * 线程池对拒绝任务（无线程可用）的处理策略，目前只支持AbortPolicy、CallerRunsPolicy；默认为后者
     * AbortPolicy:直接抛出java.util.concurrent.RejectedExecutionException异常
     * CallerRunsPolicy:主线程直接执行该任务，执行完之后尝试添加下一个任务到线程池中，可以有效降低向线程池内添加任务的速度
     * DiscardOldestPolicy:抛弃旧的任务、暂不支持；会导致被丢弃的任务无法再次被执行
     * DiscardPolicy:抛弃当前任务、暂不支持；会导致被丢弃的任务无法再次被执行
     */
    @Bean
    @Primary
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 核心线程数，默认为1
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        // 最大线程数，默认为Integer.MAX_VALUE
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        // 队列最大长度，一般需要设置值: 大于等于notifyScheduledMainExecutor.maxNum；默认为Integer.MAX_VALUE
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        // 线程池维护线程所允许的空闲时间，默认为60s
        threadPoolTaskExecutor.setKeepAliveSeconds(threadPoolKeepAliveSeconds);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix("mat--thread");
        //执行初始化  会自动执行afterPropertiesSet()初始化:
        log.info("[线程池初始化] 核心线程数[{}],最大线程数[{}],队列最大长度[{}],维护空闲时间[{}]",
                corePoolSize, maxPoolSize, queueCapacity, threadPoolKeepAliveSeconds);
        return threadPoolTaskExecutor;
    }

}
