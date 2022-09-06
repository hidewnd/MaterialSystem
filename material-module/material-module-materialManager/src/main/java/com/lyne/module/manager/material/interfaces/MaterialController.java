package com.lyne.module.manager.material.interfaces;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.manager.material.application.service.MaterialService;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.infrastructure.domain.MaterialData;
import com.lyne.module.manager.material.infrastructure.utils.ContentStyle;
import com.lyne.module.manager.material.interfaces.dto.MaterialDto;
import com.lyne.module.manager.material.interfaces.dto.MaterialInfoDto;
import com.lyne.module.manager.material.interfaces.dto.TypeDto;
import com.lyne.module.manager.material.interfaces.facade.MaterialFacade;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 物料接口
 * 业务操作
 *
 * @author lyne
 */
@RestController("material_manager_material_controller")
@RequestMapping("/material")
@CacheConfig(cacheNames = "material", cacheManager = "redisCacheManager")
public class MaterialController {
    @Resource
    private MaterialFacade facade;
    @Resource
    private MaterialService materialService;
    @Resource
    private FeignMaterialService feignMaterialService;

    //======================
    //      修改服务
    //======================

    @ApiOperation("物料信息新增")
    @PostMapping("/insert")
    public R<String> insertMaterial(@RequestBody MaterialDto dto,
                                    @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        Material material = facade.convertMaterial(dto);
        material.setCreateBy(username);
        material.setUpdateBy(username);
        String id = materialService.generalMaterial(material);
        return id != null ? R.ok("添加成功", id) : R.fail("添加失败");
    }

    @ApiOperation("物料类型信息新增")
    @PostMapping("/type/insert")
    public R<String> insertType(@RequestBody TypeDto dto,
                                @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        MaterialType type = facade.convertType(dto);
        type.setCreateBy(username);
        type.setUpdateBy(username);
        String id = materialService.generalType(type);
        return id != null ? R.ok("添加成功", id) : R.fail("添加失败");
    }

    @ApiOperation("物料信息更新")
    @PostMapping("/update")
    public R<?> updateMaterial(@RequestBody MaterialDto dto,
                               @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        Material material = facade.convertMaterial(dto);
        DataUtil.checkNulls(material.getMaterialId());
        return materialService.updateMaterial(material, username) ? R.okMsg("物料信息更新成功") : R.fail("物料信息更新失败");
    }

    @ApiOperation("物料类型信息更新")
    @PostMapping("/type/update")
    public R<?> updateMaterialType(@RequestBody TypeDto dto,
                                   @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        MaterialType type = facade.convertType(dto);
        DataUtil.checkNulls(type.getTypeId());
        return materialService.updateMaterialType(type, username) ? R.okMsg("物料类型信息更新成功") : R.fail("物料类型信息更新失败");
    }

    @ApiOperation("物料信息删除")
    @PostMapping("/delete")
    public R<?> deleteMaterial(@RequestBody MaterialDto dto,
                               @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        Assert.notNull(username, "操作人员不能为空");
        DataUtil.checkNulls(dto.getMaterialId());
        return materialService.deleteMaterial(dto.getMaterialId(), username)
                ? R.okMsg("物料信息删除成功") : R.fail("物料信息删除失败");
    }

    @ApiOperation("物料类型信息删除")
    @PostMapping("/type/delete")
    public R<?> deleteMaterialType(@RequestBody TypeDto dto,
                                   @RequestHeader(value = SecurityConstants.TOKEN_USERNAME, required = false) String username) {
        Assert.notNull(username, "操作人员不能为空");
        DataUtil.checkNulls(dto.getTypeId());
        return materialService.deleteMaterialType(dto.getTypeId(), username)
                ? R.okMsg("物料类型信息删除成功") : R.fail("物料类型信息删除失败");
    }

    //======================
    //       查询服务
    //======================

    /**
     * 获取记录中物料状态
     * 已出库 | 待出库 | 未找到
     */
    @ApiOperation("获取记录中物料状态")
    @PostMapping("/status")
//    @Cacheable(key = "'status:' + #recordId + ':' + #materialId", unless = "#result==null")
    public R<String> queryStatus(@ApiParam("记录ID") @RequestParam("recordId") String recordId,
                                 @ApiParam("物料ID") @RequestParam("materialId") String materialId) {
        DataUtil.checkNulls(recordId, materialId);
        String materialState = materialService.getMaterialState(recordId, materialId);
        return R.ok("物料状态查询成功", materialState);
    }

    @ApiOperation("获取物料位置")
    @PostMapping("/location")
    @Cacheable(key = "'location:' + #rackId + ':' + #materialId", unless = "#result==null")
    public R<List<String>> queryLocation(@ApiParam("货架ID") @RequestParam("rackId") String rackId,
                                         @ApiParam("物料ID") @RequestParam("materialId") String materialId) {
        DataUtil.checkNulls(rackId, materialId);
        List<String> location = materialService.getLocation(rackId, materialId);
        return R.ok("获取物料位置成功", location);
    }


    @ApiOperation("物料相关单值查询")
    @PostMapping("/query/one")
//    @Cacheable(key = "'query:one:' + #materialId + ':' + #type", unless = "#result==null")
    public R<?> queryOne(@ApiParam("物料ID") @RequestParam("id") String id,
                         @ApiParam("信息类型") @RequestParam(name = "type", required = false) String type) {
        DataUtil.checkNulls(id);
        return R.ok("查询物料相关信息列表成功", materialService.queryOne(facade.getQueryType(type), id));
    }

    @ApiOperation("多数据查询")
    @PostMapping("/query/list")
    public R<?> queryList(@RequestParam("type") String type,
                          @RequestParam("way") String way,
                          @RequestParam(value = "column", required = false) String column,
                          @RequestParam(value = "arg1", required = false) String arg1) {
        DataUtil.checkNulls(type, way);
        return feignMaterialService.queryList(type, way, column, arg1, SecurityConstants.INNER);
    }

    @ApiOperation("物料相关分页查询")
    @PostMapping("/query/page")
//    @Cacheable(key = "'query:page:' + #type + ':' + #size + ':' + #page + ':' + #arg1+ ':' + #arg2", unless = "#result==null")
    public R<PageObject<?>> queryPage(@ApiParam("信息类型") @RequestParam(name = "type") String type,
                                      @ApiParam("页大小") @RequestParam(name = "size", required = false) String size,
                                      @ApiParam("页序号") @RequestParam(name = "page", required = false) String page,
                                      @ApiParam("参数一") @RequestParam(name = "arg1", required = false) String arg1,
                                      @ApiParam("参数二") @RequestParam(name = "arg2", required = false) String arg2) {
        return R.ok("查询物料相关信息列表成功", materialService.queryList(facade.getQueryType(type),
                Convert.toInt(size), Convert.toInt(page), arg1, arg2));
    }

    @ApiOperation("查询货架中物料详情")
    @PostMapping("/query/info/one")
//    @Cacheable(key = "'query:info:one:' + #rackId + ':' + #matId", unless = "#result==null")
    public R<MaterialInfo> queryOneInfo(@ApiParam("货架ID") @RequestParam("rackId") String rackId,
                                        @ApiParam("入库ID") @RequestParam("recordId") String recordId,
                                        @ApiParam("物料ID") @RequestParam("materialId") String matId) {
        DataUtil.checkNulls(rackId, matId);
        return R.ok("查询物料详情信息列表成功", materialService.queryInfo(rackId, recordId, matId));
    }

    @ApiOperation("查询物料详情列表")
    @PostMapping("/query/info/list")
//    @Cacheable(key = "'query:info:list:' + #matId", unless = "#result==null")
    public R<List<MaterialInfo>> queryInfoList(@ApiParam("物料ID") @RequestParam("materialId") String matId) {
        DataUtil.checkNulls(matId);
        return R.ok("查询物料详情信息列表成功", materialService.queryInfoList(matId));
    }

    @ApiOperation("条件查询物料详情列表")
    @PostMapping("/query/info/list/arg")
    public R<PageObject<MaterialInfo>> queryInfoListByArg(@ApiParam("条件实体") @RequestBody MaterialInfoDto dto) {
        DataUtil.checkNulls(dto);
        return R.ok("查询物料详情信息列表成功", materialService.queryInfoListByArg(dto.getDepotId(),
                dto.getRackId(), dto.getMaterialId(), dto.getPage(), dto.getSize()));
    }

    @ApiOperation("查询物料库存")
    @PostMapping("/query/capacity")
//    @Cacheable(key = "'query:capacitry:' + #materialId", unless = "#result==null")
    public R<Long> queryCapacity(@ApiParam("物料ID") @RequestParam("materialId") String materialId) {
        DataUtil.checkNulls(materialId);
        return R.ok("查询物料库存成功", materialService.queryCapacity(materialId));
    }

    @ApiOperation("查询物料库存")
    @PostMapping("/query/capacity/all")
//    @Cacheable(key = "'query:all:capacitry'", unless = "#result==null")
    public R<Map<String, Long>> queryAllCapacity() {
        return R.ok("查询物料库存成功", materialService.queryAllCapacity());
    }


    @ResponseBody
    @ApiOperation("导出登记物料数据")
    @PostMapping("/export/excel")
    public void downloadExcel(HttpServletResponse response) throws Exception {
        String fileName = URLEncoder.encode(DateUtil.today() + "日登记物料.xlsx", StandardCharsets.UTF_8.toString());
        List<MaterialVo> list = MongoDBService.findAll(MaterialVo.class);
        //文件扩展名为excel格式
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //触发文件名为filename的“另存为”对话框
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        // 内容样式
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = ContentStyle.getContentStyle();
        EasyExcel.write(response.getOutputStream(), MaterialData.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .excelType(ExcelTypeEnum.XLSX)
                .autoCloseStream(true)
                .sheet("模板")
                .doWrite(list);
    }

}
