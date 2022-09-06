package com.lyne.module.manager.material.infrastructure.task;

import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.module.manager.material.application.service.RackService;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.annotation.Resource;

/**
 * 15分钟执行一次
 * 仓库容量阈值检测
 * @author lyne
 */
@Component
public class CapacityMonitoringTask {

    private static final Logger log = LoggerFactory.getLogger(CapacityMonitoringTask.class);

    @Resource
    private RackService rackService;
    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor executorService;

    /**
     * 容量监听
     */
    @Scheduled(cron = "0 0/15 * * * ?")
    public void capacityListener() {
        executorService.execute(() -> {
            log.info("[定时任务] 开始执行仓库库存容量监测");
            try {
                List<DepotVo> list = MongoDBService.findAll(DepotVo.class);
                for (DepotVo depot : list) {
                    Long maxCapacity = depot.getMaxCapacity();
                    Long threshold = depot.getThreshold();
                    List<String> rackList = depot.getRackList();
                    if (rackList.size() > 0) {
                        long sum = rackList.stream()
                                .mapToLong(aLong -> rackService.getPracticalCapacity(aLong))
                                .sum();
                        depot.setCapacity(sum);
                        if (maxCapacity != 0 && (threshold <= sum || (maxCapacity * 0.98) <= sum || sum >= maxCapacity)) {
                            log.warn("[定时任务 容量告警] [{}]仓库容量即将不足！请尽快处理, 当前容量: {}, 仓库最大可容纳容量： {}",
                                    depot.getDepotId(), sum, maxCapacity);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("[定时任务 ] 容量监测异常, 异常原因: {}", e.getMessage());
            }
        });
    }

    @Scheduled(cron = "* 0/5 * * * ?")
    public void syncRackCapacityListener() {
        executorService.execute(() -> {
            try {
                List<RackVo> list = MongoDBService.findAll(RackVo.class);
                if (list.size() > 0) {
                    for (RackVo rackVo : list) {
                        Long actualUse = rackService.getPracticalCapacity(rackVo.getRackId());
                        rackVo.setCapacity(actualUse);
                        MongoDBService.save(rackVo);
                    }
                }
            } catch (Exception e) {
                log.error("[定时任务 ] 货架容量统计异常, 异常原因: {}", e.getMessage());
            }
        });
    }
}
