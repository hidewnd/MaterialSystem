package com.lyne.module.manager.material.infrastructure.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialTypeVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.domain.material.repository.DepotRepository;
import com.lyne.module.manager.material.domain.material.repository.MaterialRepository;
import com.lyne.module.manager.material.domain.material.repository.MaterialTypeRepository;
import com.lyne.module.manager.material.domain.material.repository.RackRepository;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.domain.record.repository.RecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.annotation.Resource;

/**
 * 15分钟执行一次
 * 数据自动持久化任务
 * @author lyne
 */
@Component
public class DataSyncTask {
    private static final Logger log = LoggerFactory.getLogger(DataSyncTask.class);
    @Resource
    private DepotRepository depotRepository;
    @Resource
    private RackRepository rackRepository;
    @Resource
    private MaterialRepository materialRepository;
    @Resource
    private RecordRepository recordRepository;
    @Resource
    private MaterialTypeRepository materialTypeRepository;

    @Resource(name = "threadPoolTaskExecutor")
    private ThreadPoolTaskExecutor executorService;

    /**
     * 定时常量
     */
    private static final String cron = "0 15/10 * * * ?";
    private static final String logInfoMsg = "[定时任务 数据持久化] {}自动持久化完成, 执行时间:{}ms";

    @Scheduled(cron = cron)
    public void saveDepot() {
        executorService.execute(() -> {
            TimeInterval timer = DateUtil.timer();
            List<DepotVo> list = MongoDBService.findAll(DepotVo.class);
            if (list.size() > 0) {
                for (DepotVo vo : list) {
                    depotRepository.save(vo);
                }
            }
            log.info(logInfoMsg, "仓库实体", timer.interval());
        });

    }

    @Scheduled(cron = cron)
    public void saveRack() {
        executorService.execute(() -> {
            TimeInterval timer = DateUtil.timer();
            List<RackVo> list = MongoDBService.findAll(RackVo.class);
            if (list.size() > 0) {
                for (RackVo vo : list) {
                    rackRepository.save(vo);
                }
            }
            log.info(logInfoMsg, "货架实体", timer.interval());
        });
    }

    @Scheduled(cron = cron)
    public void saveMaterial() {
        executorService.execute(() -> {
            TimeInterval timer = DateUtil.timer();
            List<MaterialVo> list = MongoDBService.findAll(MaterialVo.class);
            if (list.size() > 0) {
                for (MaterialVo vo : list) {
                    materialRepository.save(vo);
                }
            }
            log.info(logInfoMsg, "货架实体", timer.interval());
        });
    }

    @Scheduled(cron = cron)
    public void saveMaterialType() {
        executorService.execute(() -> {
            TimeInterval timer = DateUtil.timer();
            List<MaterialTypeVo> list = MongoDBService.findAll(MaterialTypeVo.class);
            if (list.size() > 0) {
                for (MaterialTypeVo vo : list) {
                    materialTypeRepository.save(vo);
                }
            }
            log.info(logInfoMsg, "物料实体", timer.interval());
        });
    }

    @Scheduled(cron = cron)
    public void saveEnterRecord() {
        executorService.execute(() -> {
            TimeInterval timer = DateUtil.timer();
            List<EnterRecordVo> list = MongoDBService.findAll(EnterRecordVo.class);
            if (list.size() > 0) {
                for (EnterRecordVo vo : list) {
                    recordRepository.save(vo);
                }
            }
            log.info(logInfoMsg, "入库记录实体", timer.interval());
        });
    }

    @Scheduled(cron = cron)
    public void saveOuterRecord() {
        executorService.execute(() -> {
            TimeInterval timer = DateUtil.timer();
            List<OuterRecordVo> list = MongoDBService.findAll(OuterRecordVo.class);
            if (list.size() > 0) {
                for (OuterRecordVo vo : list) {
                    recordRepository.save(vo);
                }
            }
            log.info(logInfoMsg, "出库记录实体", timer.interval());
        });
    }


}
