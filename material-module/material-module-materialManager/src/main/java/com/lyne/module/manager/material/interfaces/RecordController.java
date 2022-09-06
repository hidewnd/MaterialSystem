package com.lyne.module.manager.material.interfaces;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.manager.material.application.service.RecordService;
import com.lyne.module.manager.material.infrastructure.domain.EnterMaterialVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;
import com.lyne.module.manager.material.infrastructure.domain.RecordData;
import com.lyne.module.manager.material.infrastructure.utils.ContentStyle;
import com.lyne.module.manager.material.interfaces.dto.OuterDto;
import com.lyne.module.manager.material.interfaces.dto.RecordDto;
import com.lyne.module.manager.material.interfaces.facade.RecordFacade;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 进出库接口
 *
 * @author lyne
 */
@RestController
@RequestMapping("/record")
@CacheConfig(cacheNames = "record", cacheManager = "redisCacheManager")
public class RecordController {
    @Resource
    private RecordFacade recordFacade;
    @Resource
    private RecordService recordService;

    //======================
    //       通用API
    //======================


    @ApiOperation("出入库记录负责人查询")
    @PostMapping("/query/voucher")
    @Cacheable(key = "'voucher:' + #recordId", unless = "#result==null")
    public R<String> getVoucher(@ApiParam("记录ID") @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        return R.ok("获取负责人成功", recordService.getVoucher(recordId));
    }

    @ApiOperation("查询出入库记录状态")
    @PostMapping("/query/status")
    public R<String> queryStatue(@ApiParam("记录ID") @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        String statue = recordService.queryStatue(recordId);
        return R.ok("查询出入库记录状态成功", statue);
    }


    @ApiOperation("关闭出入库记录")
    @PostMapping("/close")
    public R<?> closeRecord(@ApiParam("记录ID") @RequestParam("recordId") String recordId,
                            @RequestHeader(SecurityConstants.TOKEN_USER_ID) String accountId) {
        DataUtil.checkNulls(recordId, accountId);
        recordService.closeRecord(recordId, accountId);
        return R.okMsg("关闭成功");
    }

    @ApiOperation("出入库记录申请审批")
    @PostMapping("/approval")
    public R<?> approval(@ApiParam("记录ID") @RequestParam("recordId") String recordId,
                         @ApiParam("操作类型") @RequestParam("operation") String operation,
                         @ApiParam("审核结果") @RequestParam("result") Integer result,
                         @RequestHeader(SecurityConstants.TOKEN_USER_ID) String accountId) {
        Assert.notNull(accountId, "操作用户信息不能为空");
        DataUtil.checkNulls(recordId, result, accountId, operation);
        return recordService.approval(recordId, result, operation, accountId)
                ? R.okMsg("出入库审批成功") : R.fail("出入库审批失败");
    }

    @ApiOperation("查询出入库申请单")
    @PostMapping("/query/approval")
    public R<?> queryApproval(@RequestHeader(SecurityConstants.TOKEN_USER_ID) String accountId) {
        Assert.notNull(accountId, "操作用户信息不能为空");
        return R.ok("查询出入库申请单成功", recordService.queryApproval(accountId));
    }


    //======================
    //      入库API
    //======================

    @ApiOperation("生成入库记录")
    @PostMapping("/enter/insert")
    public R<String> generalEnter(@RequestBody(required = false) RecordDto dto,
                                  @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        Registration registration = recordFacade.convert(dto);
        registration.setCreateBy(username);
        registration.setUpdateBy(registration.getCreateBy());
        String recordId = recordService.generateEnter(registration);
        return recordId == null ? R.fail("生成出入库记录失败") : R.ok("生成出入库记录成功", recordId);
    }

    /**
     * 入库记录进行绑定物料
     */
    @ApiOperation("入库记录登记物料")
    @PostMapping("/enter/bind")
    public R<?> enterBind(@RequestBody RecordDto dto,
                          @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        DataUtil.checkNulls(dto);
        recordService.enterRecord(dto.getRecordId(), dto.getMaterialId(), dto.getNumber(), username);
        return R.okMsg("该入库记录登记物料成功");
    }

    @ApiOperation("入库记录物料正式入库")
    @PostMapping("/enter/save")
    public R<Object> enterSaveByAssign(@RequestBody RecordDto dto,
                                       @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        DataUtil.checkNulls(dto.getRecordId());
        if (dto.getMaterialId() != null && dto.getRackId() != null) {
            EnterMaterialVo resultMap;
            if (dto.getNumber() != null) {
                resultMap = recordService.enterSaveRecord(dto.getRecordId(), dto.getRackId(),
                        dto.getMaterialId(), dto.getNumber(), username);
                return R.ok("该入库记录入库成功,剩余未入库:" + resultMap.getRemain(), resultMap.getQrcode());
            }
            resultMap = recordService.enterSaveRecord(dto.getRecordId(), dto.getRackId(),
                    dto.getMaterialId(), username);
            return R.ok("该入库记录入库成功,剩余未入库:" + resultMap.getRemain(), resultMap.getQrcode());
        }
        return R.ok("该入库记录全部入库成功", recordService.enterSaveRecord(dto.getRecordId(), username));
    }

    @ApiOperation("查询入库记录待入库物料清单")
    @PostMapping("/enter/list/waiting")
    public R<?> queryEnterMaterialList(@ApiParam("人库记录ID")
                                       @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        return R.ok("查询待入库物料清单", recordService.queryEnterMaterialList(recordId));
    }

    @ApiOperation("查询入库记录已登记物料清单")
    @PostMapping("/enter/list/bind")
    public R<?> queryEnterBindList(@ApiParam("人库记录ID")
                                   @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        return R.ok("查询已登记物料清单", recordService.queryEnterBindList(recordId));
    }


    //======================
    //      出库API
    //======================

    @ApiOperation("生成出库记录")
    @PostMapping("/outer/insert")
    public R<String> generalOuter(@RequestBody(required = false) RecordDto dto,
                                  @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        Registration registration = recordFacade.convert(dto);
        registration.setCreateBy(StrUtil.isBlank(username) ? "system" : username);
        registration.setUpdateBy(registration.getCreateBy());
        String recordId = recordService.generateOuter(registration, dto.getOuterMaterial());
        return recordId == null ? R.fail("生成出库记录失败") : R.ok("生成出库记录成功", Convert.toStr(recordId));
    }

    @ApiOperation("出库记录获取出库凭证")
    @PostMapping("/outer/proof")
    @Cacheable(key = "'query:proof:' + #recordId", unless = "#result==null")
    public R<String> getOuterProof(@ApiParam("出库记录ID") @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        String proof = recordService.getProof(recordId);
        return R.ok("获取出库记录凭证成功", proof);
    }

    @ApiOperation("出库记录获取待取物料地址")
    @PostMapping("/outer/locations")
//    @Cacheable(key = "'query:locations:' + #recordId", unless = "#result==null")
    public R<Map<String, List<String>>> getOuterLocations(@ApiParam("出库记录ID") @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        return R.ok("获取物料地址信息成功", recordService.getLocations(recordId));
    }

    @ApiOperation("出库记录获取所有物料二维码")
    @PostMapping("/outer/qrcode/all")
//    @Cacheable(key = "'query:qrcode:all:' + #recordId", unless = "#result==null")
    public R<Map<String, List<String>>> getAllQrCode(@ApiParam("出库记录ID") @RequestParam("recordId") String recordId) {
        Map<String, List<String>> map = recordService.getAllQrCodes(recordId);
        return R.ok("获取记录所有二维码数据成功", map);
    }

    @ApiOperation("出库记录获取物料二维码")
    @PostMapping("/outer/qrcode")
//    @Cacheable(key = "'query:qrcode:one:' + #recordId", unless = "#result==null")
    public R<List<String>> getQrCode(@ApiParam("出库记录ID") @RequestParam("recordId") String recordId,
                                     @RequestParam("materialId") String materialId) {
        return R.ok("获取二维码数据成功", recordService.getQrCodes(recordId, materialId));
    }

    @ApiOperation("查询出库物料清单")
    @PostMapping("/outer/matList")
    public R<Map<String, Map<Long, String>>> queryMaterialList(@ApiParam("出库记录ID") @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        return R.ok("查询出库物流清单成功", recordService.queryOuterMaterialList(recordId));
    }


    @ApiOperation("出库记录核销物料")
    @PostMapping("/outer/verify")
    public R<String> outerRecord(@RequestBody OuterDto dto,
                                 @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        DataUtil.checkNulls(dto.getRecordId(), dto.getProof(), dto.getQrcode());
        recordService.outRecord(dto.getRecordId(), dto.getProof(), dto.getQrcode(),
                StrUtil.isBlank(username) ? "system" : username);
        return R.okMsg("核销成功");
    }


    //======================
    //     CRUD API
    //======================

    /**
     * 单值查询
     *
     * @param recordId 记录id
     * @param type     类型
     * @return Registration
     */
    @ApiOperation("出入库记录单值查询")
    @PostMapping("/query/one")
//    @Cacheable(key = "'query:one:' + #type + ':' + #recordId", unless = "#result==null")
    public R<RecordVo> queryOne(@ApiParam("记录类型") @RequestParam(value = "type", required = false) String type,
                                @ApiParam("记录ID") @RequestParam("recordId") String recordId) {
        DataUtil.checkNulls(recordId);
        RecordVo vo = recordService.queryOne(recordId, recordFacade.getType(type));
        return R.ok("查询出入库记录信息成功", vo);
    }

    /**
     * 多值查询
     *
     * @param type 类型
     * @param size 每页大小
     * @param page 页序号
     * @param arg1 条件参数一
     * @param arg2 条件参数二
     */
    @ApiOperation("出入库记录分页查询")
    @PostMapping("/query/page")
//    @Cacheable(key = "'query:page:' + #type + ':' + #size + ':' + #page + ':' + #arg1+ ':' + #arg2", unless = "#result==null")
    public R<PageObject<?>> queryPage(@RequestParam(name = "type", required = false) String type,
                                      @RequestParam(name = "size", required = false) Integer size,
                                      @RequestParam(name = "page", required = false) Integer page,
                                      @RequestParam(name = "arg1", required = false) String arg1,
                                      @RequestParam(name = "arg2", required = false) String arg2) {
        return R.ok("查询出入库记录成功", recordService.queryList(recordFacade.getType(type), size, page, arg1, arg2));
    }


    @ApiOperation("出入库修改")
    @PostMapping("/update")
    public R<?> updateRecord(@ApiParam("出入库实体") @RequestBody RecordDto dto,
                             @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        Assert.notNull(dto.getRecordId(), "出入库ID不能为空");
        Registration registration = recordFacade.convert(dto);
        return recordService.updateRecord(registration, username)
                ? R.okMsg("修改出入库信息成功") : R.fail("修改出入库信息失败");
    }

    @ApiOperation("出入库删除")
    @PostMapping("/delete")
    public R<?> deleteRecord(@ApiParam("出入库ID") @RequestParam RecordDto dto,
                             @RequestHeader(SecurityConstants.TOKEN_USERNAME) String username) {
        Assert.notNull(username, "操作人员不能为空");
        Assert.notNull(dto.getRecordId(), "出入库ID不能为空");
        return recordService.deleteRecord(dto.getRecordId(), username)
                ? R.okMsg("删除出入库信息成功") : R.fail("删除出入库信息失败");
    }


    @ResponseBody
    @ApiOperation("导出出入库数据")
    @PostMapping("/export/excel")
    public void downloadExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        String fileName = URLEncoder.encode(DateUtil.today() + "出入库记录.xlsx", StandardCharsets.UTF_8.toString());
        HorizontalCellStyleStrategy horizontalCellStyleStrategy = ContentStyle.getContentStyle();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ArrayList<RecordData> list = new ArrayList<>();
        List<EnterRecordVo> enterList = MongoDBService.findAll(EnterRecordVo.class);
        if (enterList.size() > 0) {
            enterList.forEach(enterRecordVo -> list.add(new RecordData(enterRecordVo)));
        }
        List<OuterRecordVo> outerRecordVoList = MongoDBService.findAll(OuterRecordVo.class);
        if (outerRecordVoList.size() > 0) {
            outerRecordVoList.forEach(outer -> list.add(new RecordData(outer)));
        }

        EasyExcel.write(response.getOutputStream(), RecordData.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .excelType(ExcelTypeEnum.XLSX)
                .autoCloseStream(true)
                .sheet("模板")
                .doWrite(list);
    }

}
