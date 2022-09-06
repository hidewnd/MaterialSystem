package com.lyne.module.manager.material.infrastructure.task;

import com.lyne.module.manager.material.infrastructure.factories.DepotFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialInfoFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialTypeFactory;
import com.lyne.module.manager.material.infrastructure.factories.RackFactory;
import com.lyne.module.manager.material.infrastructure.factories.RecordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * mongo数据初始化
 * @author lyne
 */
@Component
public class MongoDbInitialTask implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoDbInitialTask.class);

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor executorService;
    @Resource
    private DepotFactory depotFactory;
    @Resource
    private RackFactory rackFactory;
    @Resource
    private MaterialFactory materialFactory;
    @Resource
    private MaterialTypeFactory typeFactory;
    @Resource
    private MaterialInfoFactory infoFactory;
    @Resource
    private RecordFactory<?> recordFactory;

    /**
     * 初始化MongoDB数据
     */
    @Override
    public void run(String... args) {
        executorService.submit(() -> {
            depotFactory.syncData();
            log.info("[MongoDB初始化] 仓库数据初始化完成");
        });
        executorService.submit(() -> {
            rackFactory.syncData();
            log.info("[MongoDB初始化] 货架数据初始化完成");
            infoFactory.syncData();
            log.info("[MongoDB初始化] 物料详情数据初始化完成");
        });
        executorService.submit(() -> {
            materialFactory.syncData();
            log.info("[MongoDB初始化] 物料数据初始化完成");
        });
        executorService.submit(() -> {
            typeFactory.syncData();
            log.info("[MongoDB初始化] 物料类型数据初始化完成");
        });
        executorService.submit(() -> {
            recordFactory.syncData();
            log.info("[MongoDB初始化] 出入库记录数据初始化完成");
        });
    }
}
