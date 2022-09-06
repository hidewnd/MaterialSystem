package com.lyne.module.manager.material.interfaces;

import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.manager.material.application.service.DepotService;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.interfaces.dto.DepotDto;
import com.lyne.module.manager.material.interfaces.facade.DepotFacade;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 仓库接口
 *
 * @author lyne
 */
@RestController
@RequestMapping("/depot")
@CacheConfig(cacheNames = "depot", cacheManager = "redisCacheManager")
public class DepotController {
    @Resource
    private DepotFacade depotFacade;
    @Resource
    private DepotService depotService;


    //======================
    //       修改服务
    //======================

    @ApiOperation("新增仓库")
    @PostMapping("/insert")
    public R<String> insertDepot(@RequestBody DepotDto dto,
                                 @RequestHeader(SecurityConstants.TOKEN_USERNAME) String operator) {
        MaterialDepot depot = depotFacade.convert(dto);
        String id = depotService.generate(depot, operator);
        return id == null || StringUtil.isEmpty(id) ? R.fail("保存失败") : R.ok(id);
    }

    @ApiOperation("仓库更新")
    @PostMapping("/update")
    public R<?> updateDepot(@RequestBody DepotDto dto,
                            @RequestHeader(SecurityConstants.TOKEN_USERNAME) String operator) {
        MaterialDepot depot = depotFacade.convert(dto);
        DataUtil.checkNulls(depot.getDepotId());
        return depotService.updateDepot(depot, operator) ? R.okMsg("仓库更新成功") : R.fail("仓库更新失败");
    }

    @ApiOperation("仓库删除")
    @PostMapping("/delete")
    public R<?> deleteDepot(@RequestBody DepotDto dto,
                            @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        Assert.notNull(username, "操作人员不能为空");
        DataUtil.checkNulls(dto.getDepotId());
        return depotService.deleteDepot(dto.getDepotId(), username) ? R.okMsg("仓库删除成功") : R.fail("仓库删除失败");
    }


    @ApiOperation("移动货架")
    @PostMapping("/transfer")
    public R<?> transferRack(@ApiParam("货架ID") @RequestParam("rackId") String rackId,
                             @ApiParam("原有仓库ID") @RequestParam("oldId") String oldId,
                             @ApiParam("目前仓库ID") @RequestParam("targetId") String newId,
                             @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(rackId, oldId, newId);
        return depotService.transferRack(oldId, newId, rackId, operator) ? R.okMsg("移动成功") : R.fail("移动失败");
    }

    @ApiOperation("设置容量阈值")
    @PostMapping("/set/threshold")
    public R<Object> setThreshold(@ApiParam("仓库ID") @RequestParam("depotId") String depotId,
                                  @ApiParam("阈值") @RequestParam("threshold") Long threshold,
                                  @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String operator) {
        DataUtil.checkNulls(depotId, threshold);
        return depotService.updateThreshold(depotId, threshold, operator) ? R.okMsg("设置阈值成功") : R.fail("设置阈值失败");
    }

    //======================
    //       查询服务
    //======================

    @ApiOperation("仓库单值查询")
    @PostMapping("/query/one")
//    @Cacheable(key = "'query:one:' + #depotId", unless = "#result==null")
    public R<?> queryOne(@ApiParam("仓库ID") @RequestParam("depotId") String depotId) {
        if (!StringUtil.isNumeric(depotId)) {
            return R.fail("id值为数字");
        }
        return R.ok("查询仓库信息成功", depotService.queryOne(depotId));
    }


    @ApiOperation("多值分页查询")
    @PostMapping("/query/page")
//    @Cacheable(key = "'query:page:' + #size + ':' + #page + ':' + #arg1+ ':' + #arg2", unless = "#result==null")
    public R<PageObject<DepotVo>> queryList(@ApiParam("页大小") @RequestParam(name = "size", required = false) Integer size,
                                            @ApiParam("页序号") @RequestParam(name = "page", required = false) Integer page,
                                            @ApiParam("参数一") @RequestParam(name = "arg1", required = false) String arg1,
                                            @ApiParam("参数二") @RequestParam(name = "arg2", required = false) String arg2) {
        return R.ok("查询仓库信息列表成功", depotService.queryList(size, page, arg1, arg2));
    }
}
