package com.lyne.module.manager.material.interfaces;

import com.lyne.common.core.base.R;
import com.lyne.module.manager.material.application.service.StatisticsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 统计数据接口
 * @author lyne
 */
@RestController
@RequestMapping("/statistics")
@CacheConfig(cacheNames = "statistics", cacheManager = "statisticRedisCacheManager")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @ApiOperation("查询库存统计")
    @PostMapping("/total/stock")
    @Cacheable(key = "'statistics:stock'", unless = "#result==null")
    public R<?> queryStockTotal() {
        return R.ok("查询库存统计数据成功", statisticsService.getStatisticsTotal());
    }

    @ApiOperation("查询物料类型库存统计")
    @PostMapping("/total/type")
    @Cacheable(key = "'statistics:type'", unless = "#result==null")
    public R<?> queryTypeStockTotal() {
        return R.ok("查询物料类型库存统计数据成功", statisticsService.getStockTypeTotal());
    }

    @ApiOperation("查询出入库记录中物料占比")
    @PostMapping("/total/type/record")
    @Cacheable(key = "'statistics:typeforrecord'", unless = "#result==null")
    public R<?> queryRecordTypeTotal() {
        return R.ok("查询出入库记录中物料占比数据统计成功", statisticsService.getRecordTypeTotal());
    }

    @ApiOperation("查询出入库记录统计")
    @PostMapping("/total/record")
    @Cacheable(key = "'statistics:record'", unless = "#result==null")
    public R<?> queryRecordTotal() {
        return R.ok("查询出入库记录统计数据成功", statisticsService.getRecordTotal());
    }

    @ApiOperation("查询仓库统计")
    @PostMapping("/total/depot")
    @Cacheable(key = "'statistics:depot'", unless = "#result==null")
    public R<?> queryDepotTotal() {
        return R.ok("查询仓库统计数据成功", statisticsService.getDepotTotal());
    }

    @ApiOperation("查询物料统计")
    @PostMapping("/total/material")
    @Cacheable(key = "'statistics:material'", unless = "#result==null")
    public R<?> queryMaterialTotal() {
        return R.ok("查询物料统计数据成功", statisticsService.getMaterialTotal());
    }
}
