package com.lyne.module.manager.material.interfaces;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.manager.material.application.service.RackService;
import com.lyne.module.manager.material.infrastructure.domain.EnterMaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.interfaces.dto.RackDto;
import com.lyne.module.manager.material.interfaces.facade.RackFacade;
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
 * 货架信息
 *
 * @author lyne
 */
@RestController
@RequestMapping("/rack")
@CacheConfig(cacheNames = "rack", cacheManager = "redisCacheManager")
public class RackController {
    @Resource
    private RackFacade rackFacade;
    @Resource
    private RackService rackService;

    //======================
    //       修改API
    //======================

    @ApiOperation("货架新增")
    @PostMapping("/insert")
    public R<String> insertRack(@RequestBody RackDto dto,
                                @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        MaterialRack rack = rackFacade.convertRack(dto);
        rack.setCreateBy(username);
        String id = rackService.generate(rack);
        return id == null ? R.fail("新增货架失败") : R.ok("新增货架成功", Convert.toStr(id));
    }

    @ApiOperation("货架更新")
    @PostMapping("/update")
    public R<?> updateRack(@RequestBody RackDto dto,
                           @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        MaterialRack rack = rackFacade.convertRack(dto);
        DataUtil.checkNulls(rack.getRackId());
        return rackService.updateRack(rack, username) ? R.okMsg("货架更新成功") : R.fail("货架更新失败");
    }

    @ApiOperation("货架删除")
    @PostMapping("/delete")
    public R<?> deleteRack(@RequestBody RackDto dto,
                           @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        Assert.notNull(username, "操作人员不能为空");
        DataUtil.checkNulls(dto.getRackId());
        return rackService.deleteRack(dto.getRackId(), username) ? R.okMsg("货架删除成功") : R.fail("货架删除失败");
    }


    @ApiOperation("货架入库物料")
    @PostMapping("/stored")
    public R<?> materialStored(@ApiParam("货架ID") @RequestParam("rackId") String rackId,
                               @ApiParam("物料ID") @RequestParam("recordId") String recordId,
                               @ApiParam("物料ID") @RequestParam("materialId") String matId,
                               @ApiParam("入库数量") @RequestParam("num") Long num,
                               @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {

        EnterMaterialVo map = rackService.stored(rackId, recordId, matId, num, StrUtil.isBlank(username) ? "system" : username);
        return R.ok("货架存入物料成功, 生成物料二维码成功" + map.getRemain(), map.getQrcode());
    }


    @ApiOperation("货架出库物料")
    @PostMapping("/takeout")
    public R<?> materialRemove(@ApiParam("货架ID") @RequestParam("rackId") String rackId,
                               @ApiParam("物料ID") @RequestParam("recordId") String recordId,
                               @ApiParam("物料ID") @RequestParam("materialId") String matId,
                               @ApiParam("入库数量") @RequestParam("num") Long num,
                               @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        rackService.takeOut(rackId, recordId, matId, num, StrUtil.isBlank(username) ? "system" : username);
        return R.okMsg("货架取出物料成功");
    }

    //======================
    //       查询API
    //======================

    @ApiOperation("货架单值查询")
    @PostMapping("/query/one")
//    @Cacheable(key = "'query:one:' + #rackId", unless = "#result==null")
    public R<?> queryOne(@ApiParam("仓库ID") @RequestParam("rackId") String rackId) {
        DataUtil.checkNulls(rackId);
        return R.ok("查询货架信息成功", rackService.queryOne(rackId));
    }

    @ApiOperation("货架分页查询")
    @PostMapping("/query/page")
//    @Cacheable(key = "'query:page:' + #size + ':' + #page + ':' + #arg1+ ':' + #arg2", unless = "#result==null")
    public R<PageObject<RackVo>> queryPage(@ApiParam("页大小") @RequestParam(name = "size", required = false) Integer size,
                                           @ApiParam("页序号") @RequestParam(name = "page", required = false) Integer page,
                                           @ApiParam("参数一") @RequestParam(name = "arg1", required = false) String arg1,
                                           @ApiParam("参数二") @RequestParam(name = "arg2", required = false) String arg2) {
        return R.ok("查询货架列表成功", rackService.queryList(size, page, arg1, arg2));
    }

}
