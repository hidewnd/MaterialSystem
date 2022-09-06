package com.lyne.module.material.controller;

import com.lyne.common.core.base.R;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.security.annotation.InnerAuth;
import com.lyne.module.material.service.QueryService;
import com.lyne.module.material.service.RackService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 查询接口
 *
 * @author lyne
 */
@RestController
@RequestMapping("/query")
public class QueryController {
    @Resource
    private RackService rackService;
    @Resource
    private QueryService queryService;

    @ApiOperation("单数据查询接口")
    @GetMapping(value = {"/one/{name}/{type}/{arg1}", "/one/{name}/{type}/{arg1}/{arg2}"})
    public R<?> queryOne(@ApiParam("数据类型") @PathVariable(value = "name") String name,
                         @ApiParam("查询方式") @PathVariable("type") String type,
                         @ApiParam("参数一") @PathVariable(name = "arg1", required = false) String arg1,
                         @ApiParam("参数二") @PathVariable(name = "arg2", required = false) String arg2) {
        DataUtil.checkNulls(name, type);
        return queryService.queryOne(name, type, arg1, arg2);
    }

    @InnerAuth
    @ApiOperation("多数据条件查询接口")
    @GetMapping(value = {"/list/{name}/{type}",
            "/list/{name}/{type}/{arg1}",
            "/list/{name}/{type}/{column}/{arg1}",
            "/list/{name}/{type}/{column}/{arg1}/{arg2}"})
    public R<?> queryList(@PathVariable("name") @ApiParam("数据类型") String name,
                          @PathVariable("type") @ApiParam("查询方式") String type,
                          @PathVariable(value = "column", required = false) @ApiParam("条件字段") String column,
                          @PathVariable(value = "arg1", required = false) @ApiParam("参数一") String arg1,
                          @PathVariable(value = "arg2", required = false) @ApiParam("参数二") String arg2) {
        DataUtil.checkNulls(name, type);
        return queryService.queryList(name, type, column, arg1, arg2);
    }


    @ApiOperation("多数据分页查询接口")
    @GetMapping(value = {"/page/{type}",
            "/page/{type}/{size}/{page}"})
    public R<?> queryListByPage(@PathVariable("type") @ApiParam("数据类型") String type,
                                @ApiParam("页大小") @PathVariable(name = "size", required = false) Integer size,
                                @ApiParam("页码") @PathVariable(name = "page", required = false) Integer page) {
        DataUtil.checkNulls(type);
        return queryService.queryPage(type, size, page);
    }

    @ApiOperation("查询单值库存")
    @PostMapping("/one/stock")
    public R<Material> queryStockByOne(@ApiParam("货架ID") @RequestParam(value = "rackId") String rackId,
                                       @ApiParam("入库ID") @RequestParam(value = "recordId", required = false) String recordId,
                                       @ApiParam("物料ID") @RequestParam(value = "materialId") String materialId) {
        DataUtil.checkNulls(rackId, materialId);
        return R.ok("查询物料库存成功", rackService.getMaterial(rackId, recordId, materialId));
    }

    @ApiOperation("查询库存列表")
    @PostMapping("/list/stock")
    public R<?> queryStockByList(@ApiParam("货架ID") @RequestParam(value = "rackId", required = false) String rackId,
                                 @ApiParam("入库ID") @RequestParam(value = "recordId", required = false) String recordId,
                                 @ApiParam("物料ID") @RequestParam(value = "materialId", required = false) String materialId) {
        return R.ok("查询库存列表成功", rackService.getMaterialList(rackId, recordId, materialId));
    }

    @ApiOperation("分页查询库存列表")
    @GetMapping(value = {"/page/stock", "/page/stock/{size}/{page}"})
    public R<?> queryStockByPage(@ApiParam("页大小") @PathVariable(name = "size", required = false) Integer size,
                                 @ApiParam("页码") @PathVariable(name = "page", required = false) Integer page) {
        return R.ok("分页查询库存列表成功", rackService.queryStockByPage(size, page));
    }


}
