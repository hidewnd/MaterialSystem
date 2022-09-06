package com.lyne.api.material.service;

import com.lyne.api.material.config.MaterialFeignInterceptor;
import com.lyne.api.material.factory.FeignQueryFactory;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 物料基础模块远程调度
 *
 * @author lyne
 */
@Component
@FeignClient(contextId = "feignServiceMaterial",
        value = "material-module-material",
        fallbackFactory = FeignQueryFactory.class,
        configuration = {MaterialFeignInterceptor.class})
public interface FeignMaterialService {

    //=======================
    //       单数据查询
    //=======================

    /**
     * 单值查询
     * @param name      数据类型
     * @param type       查询方式
     * @param arg1      参数一
     * @param source    请求源
     * @return R
     */
    @GetMapping("/query/one/{name}/{type}/{arg1}")
    R<?> queryOne(@PathVariable("name") String name,
                  @PathVariable("type") String type,
                  @PathVariable(name = "arg1", required = false) String arg1,
                  @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 单值查询
     * @param name      数据类型
     * @param type       查询方式
     * @param arg1      参数一
     * @param arg2      参数二
     * @param source    请求源
     * @return R
     */
    @GetMapping("/query/one/{name}/{type}/{arg1}/{arg2}")
    R<?> queryOne(@PathVariable("name") String name,
                  @PathVariable("type") String type,
                  @PathVariable(name = "arg1", required = false) String arg1,
                  @PathVariable(name = "arg2", required = false) String arg2,
                  @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 查询单值库存
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return R
     */
    @PostMapping("/query/one/stock")
    R<?> queryStockByOne(@RequestParam(value = "rackId") String rackId,
                         @RequestParam(value = "recordId", required = false) String recordId,
                         @RequestParam(value = "materialId") String materialId,
                         @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    //=======================
    //       多数据查询
    //=======================

    /**
     * 多数据查询
     * @param type      数据类型
     * @param way       查询方式
     * @param source    请求源
     * @return R
     */
    @GetMapping("/query/list/{type}/{way}")
    R<?> queryList(@PathVariable("type") String type, @PathVariable("way") String way,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 多数据查询
     * @param type      数据类型
     * @param way       查询方式
     * @param column    条件字段
     * @param source    请求源
     * @return R
     */
    @GetMapping("/query/list/{name}/{type}/{column}")
    R<?> queryList(@PathVariable("name") String type,
                   @PathVariable("type") String way,
                   @PathVariable(value = "column", required = false) String column,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 多数据查询
     * @param type      数据类型
     * @param way       查询方式
     * @param arg1      条件参数
     * @param source    请求源
     * @return R
     */
    @GetMapping("/query/list/{name}/{type}/{arg1}")
    R<?> queryList2(@PathVariable("name") String type,
                    @PathVariable("type") String way,
                    @PathVariable(value = "arg1", required = false) String arg1,
                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 多数据查询
     * @param type      数据类型
     * @param way       查询方式
     * @param column    条件字段
     * @param arg1      参数一
     * @param source    请求源
     * @return R
     */
    @GetMapping("/query/list/{type}/{way}/{column}/{arg1}")
    R<?> queryList(@PathVariable("type") String type, @PathVariable("way") String way,
                   @PathVariable(value = "column", required = false) String column,
                   @PathVariable(value = "arg1", required = false) String arg1,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 多数据查询
     * @param type      数据类型
     * @param way       查询方式
     * @param column    条件字段
     * @param arg1      参数一
     * @param arg2      参数二
     * @param source    请求源
     * @return R
     */
    @GetMapping("/query/list/{type}/{way}/{column}/{arg1}/{arg2}")
    R<?> queryList(@PathVariable("type") String type, @PathVariable("way") String way,
                   @PathVariable(value = "column", required = false) String column,
                   @PathVariable(value = "arg1", required = false) String arg1,
                   @PathVariable(value = "arg2", required = false) String arg2,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 查询库存列表
     * @param rackId        货架ID
     * @param recordId      入库ID
     * @param materialId    物料ID
     * @return R
     */
    @PostMapping("/query/list/stock")
    R<?> queryStockByList(@RequestParam(value = "rackId", required = false) String rackId,
                          @RequestParam(value = "recordId", required = false) String recordId,
                          @RequestParam(value = "materialId", required = false) String materialId,
                          @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    //=======================
    //       修改操作
    //=======================

    /**
     * 新增或修改出入库记录
     * @param registration  实体
     * @param source        请求源
     * @return R
     */
    @PostMapping("/save/record")
    R<String> saveRecord(@RequestBody Registration registration,
                       @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 新增或修改物料
     * @param material      实体
     * @param source        请求源
     * @return R
     */
    @PostMapping("/save/material")
    R<String> saveMaterial(@RequestBody Material material,
                         @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 新增或修改物料类型
     * @param type          实体
     * @param source        请求源
     * @return R
     */
    @PostMapping("/save/material/type")
    R<String> saveMaterialType(@RequestBody MaterialType type,
                             @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 新增或修改仓库
     * @param depot         实体
     * @param source        请求源
     * @return R
     */
    @PostMapping("/save/depot")
    R<String> saveDepot(@RequestBody MaterialDepot depot,
                      @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 新增或修改货架
     * @param rack          实体
     * @param source        请求源
     * @return R
     */
    @PostMapping("/save/rack")
    R<String> saveRack(@RequestBody MaterialRack rack,
                     @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


    //=======================
    //       关系处理操作
    //=======================

    /**
     * 货架存入物料
     * @param materialId    物料Id
     * @param rackId        货架Id
     * @param number        数量
     * @param source        请求源
     * @return R
     */
    @PostMapping("/rack/store")
    R<?> rackStore(@RequestParam("rackId") String rackId,
                   @RequestParam(value = "recordId", required = false) String recordId,
                   @RequestParam("materialId") String materialId,
                   @RequestParam("num") Long number,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 货架取出物料
     * @param materialId    物料Id
     * @param rackId        货架Id
     * @param number        数量
     * @param source        请求源
     * @return R
     */
    @PostMapping("/rack/takeout")
    R<?> rackTackOut(@RequestParam("rackId") Long rackId,
                     @RequestParam(value = "recordId", required = false) Long recordId,
                     @RequestParam("materialId") Long materialId,
                     @RequestParam("num") Long number,
                     @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 记录添加出入库记录绑定物料
     * @param regMaterial   记录实体
     * @param source        请求源
     * @return R
     */
    @PostMapping("/record/bind/material")
    R<Long> recordBind(@RequestBody RegMaterial regMaterial,
                       @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 出入库记录修改绑定物料
     * @param regMaterial   绑定关系实体
     * @param source        请求源
     * @return R
     */
    @PostMapping("/record/updateBind/material")
    R<Long> updateRecordBind(@RequestBody RegMaterial regMaterial,
                             @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 出入库记录移除绑定物料
     * @param regId         记录ID
     * @param materialId    物料Id
     * @param source        请求源
     * @return R
     */
    @PostMapping("/record/removeBind/material")
    R<?> removeRecordBind(@RequestParam("recordId") Long regId,
                          @RequestParam("materialId") Long materialId,
                          @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 仓库绑定货架
     * @param depotId       仓库ID
     * @param rackId        货架ID
     * @param source        请求源
     * @return R
     */
    @PostMapping("/depot/bind/rack")
    R<?> depotBindRack(@RequestParam("depotId") Long depotId,
                       @RequestParam("rackId") Long rackId,
                       @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 仓库绑定货架
     * @param depotId       仓库ID
     * @param rackId        货架ID
     * @param source        请求源
     * @return R
     */
    @PostMapping("/depot/removeBind/rack")
    R<?> depotRemoveRack(@RequestParam("depotId") Long depotId,
                         @RequestParam("rackId") Long rackId,
                         @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
