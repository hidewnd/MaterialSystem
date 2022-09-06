package com.lyne.module.material.controller;

import com.lyne.common.core.base.R;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.module.material.service.DepotService;
import com.lyne.module.material.service.MaterialService;
import com.lyne.module.material.service.MaterialTypeService;
import com.lyne.module.material.service.RackService;
import com.lyne.module.material.service.RegistrationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 同步修改操作接口
 *
 * @author lyne
 */
@RestController
public class UpdateController {
    @Resource
    private RegistrationService registrationService;
    @Resource
    private MaterialService materialService;
    @Resource
    private MaterialTypeService materialTypeService;
    @Resource
    private DepotService depotService;
    @Resource
    private RackService rackService;

    //=======================
    //       添加或修改操作
    //=======================

    @ApiOperation("新增或修改出入库记录")
    @PostMapping("/save/record")
    public R<String> insertRegistration(@ApiParam("出入库记录") @RequestBody Registration registration) {
        Assert.notNull(registration, "实体不能为空");
        return registrationService.saveOrUpdate(registration)
                ? R.ok("更新成功", registration.getRegId()) : R.fail("更新失败");
    }

    @ApiOperation("新增或修改物料")
    @PostMapping("/save/material")
    public R<String> insertMaterial(@RequestBody Material material) {
        Assert.notNull(material, "实体不能为空");
        Assert.notNull(material.getMaterialName(), "物料名不能为空");
        return materialService.saveOrUpdate(material) ?
                R.ok("更新成功", material.getMaterialId()) : R.fail("更新失败");
    }

    @ApiOperation("新增或修改物料类型")
    @PostMapping("/save/material/type")
    public R<String> insertMaterialType(@RequestBody MaterialType type) {
        Assert.notNull(type, "实体不能为空");
        Assert.notNull(type.getTypeName(), "物料名不能为空");
        return materialTypeService.saveOrUpdate(type) ?
                R.ok("更新成功", type.getTypeId()) : R.fail("更新失败");
    }

    @ApiOperation("新增或修改仓库")
    @PostMapping("/save/depot")
    public R<String> insertDepot(@RequestBody MaterialDepot depot) {
        Assert.notNull(depot, "实体不能为空");
        Assert.notNull(depot.getDepotName(), "仓库名不能为空");
        return depotService.saveOrUpdate(depot) ?
                R.ok("更新成功", depot.getDepotId()) : R.fail("更新失败");
    }

    @ApiOperation("新增或修改货架")
    @PostMapping("/save/rack")
    public R<String> insertRack(@RequestBody MaterialRack rack) {
        Assert.notNull(rack, "实体不能为空");
        return rackService.saveOrUpdate(rack) ?
                R.ok("更新成功", rack.getRackId()) : R.fail("更新失败");
    }

    //=======================
    //       删除操作
    //=======================

    @ApiOperation("删除物料")
    @PostMapping("/delete/material")
    public R<?> deleteMaterial(@RequestBody Material material) {
        Assert.notNull(material, "实体不能为空");
        Assert.notNull(material.getMaterialId(), "Id不能为空");
        return materialService.removeById(material) ? R.ok("删除成功") : R.fail("删除失败");
    }

    @ApiOperation("删除物料类型")
    @PostMapping("/delete/material/type")
    public R<?> deleteMaterialType(@RequestBody MaterialType type) {
        Assert.notNull(type, "实体不能为空");
        Assert.notNull(type.getTypeId(), "Id不能为空");
        return materialTypeService.removeById(type) ? R.ok("删除成功") : R.fail("删除失败");
    }

    @ApiOperation("删除货架")
    @PostMapping("/delete/rack")
    public R<?> deleteRack(@RequestBody MaterialRack rack) {
        Assert.notNull(rack, "实体不能为空");
        Assert.notNull(rack.getRackId(), "Id不能为空");
        return rackService.removeById(rack) ? R.ok("删除成功") : R.fail("删除失败");
    }

    @ApiOperation("删除仓库")
    @PostMapping("/delete/depot")
    public R<?> deleteDepot(@RequestBody MaterialDepot depot) {
        Assert.notNull(depot, "实体不能为空");
        Assert.notNull(depot.getDepotId(), "Id不能为空");
        return depotService.removeById(depot) ? R.ok("删除成功") : R.fail("删除失败");
    }

    @ApiOperation("删除出入库记录")
    @PostMapping("/delete/record")
    public R<?> deleteRecord(@RequestBody Registration registration) {
        Assert.notNull(registration, "实体不能为空");
        Assert.notNull(registration.getRegId(), "Id不能为空");
        return registrationService.removeById(registration) ? R.ok("删除成功") : R.fail("删除失败");
    }

    //=======================
    //       关系处理操作
    //=======================

    @ApiOperation("货架存入物料")
    @PostMapping("/rack/store")
    public R<?> rackBind(@ApiParam("货架ID") @RequestParam("rackId") String rackId,
                         @ApiParam("入库记录ID") @RequestParam(value = "recordId", required = false) String recordId,
                         @ApiParam("物料ID") @RequestParam("materialId") String materialId,
                         @ApiParam("物料入库数量") @RequestParam("num") Long number) {
        DataUtil.checkNulls(materialId, rackId, number);
        return rackService.bindMaterial(rackId, recordId, materialId, number)
                ? R.okMsg("货架存入物料成功") : R.fail("货架存入物料失败");
    }

    @ApiOperation("货架取出物料")
    @PostMapping("/rack/takeout")
    public R<?> removeRackBind(@ApiParam("货架ID") @RequestParam("rackId") String rackId,
                               @ApiParam("入库记录ID") @RequestParam(value = "recordId", required = false) String recordId,
                               @ApiParam("物料ID") @RequestParam("materialId") String materialId,
                               @ApiParam("物料入库数量") @RequestParam("num") Long number) {
        DataUtil.checkNulls(materialId, rackId, number);
        return rackService.removeMaterial(rackId, recordId, materialId, number)
                ? R.okMsg("货架取出物料成功") : R.fail("货架取出物料失败");
    }


    @ApiOperation("出入库记录绑定物料")
    @PostMapping("/record/bind/material")
    public R<Long> recordBind(@RequestBody RegMaterial regMaterial) {
        Assert.notNull(regMaterial, "实体不能为空");
        return registrationService.bindMaterial(regMaterial)
                ? R.okMsg("出入库记录添加物料成功") : R.fail("出入库记录添加物料失败");
    }

    @ApiOperation("出入库记录修改绑定物料")
    @PostMapping("/record/updateBind/material")
    public R<Long> updateRecordBind(@RequestBody RegMaterial regMaterial) {
        Assert.notNull(regMaterial, "实体不能为空");
        return registrationService.updateMaterial(regMaterial)
                ? R.okMsg("出入库记录修改物料成功") : R.fail("出入库记录修改物料失败");
    }

    @ApiOperation("出入库记录解除绑定物料")
    @PostMapping("/record/removeBind/material")
    public R<?> removeRecordBind(@RequestParam("recordId") String regId, @RequestParam("materialId") String materialId) {
        DataUtil.checkNulls(regId, materialId);
        return registrationService.removeBind(regId, materialId)
                ? R.okMsg("出入库记录移除物料成功") : R.fail("出入库记录移除物料失败");
    }

    @ApiOperation("仓库绑定货架")
    @PostMapping("/depot/bind/rack")
    public R<?> depotBindRack(@RequestParam("depotId") String depotId, @RequestParam("rackId") String rackId) {
        DataUtil.checkNulls(depotId, rackId);
        return depotService.bindRack(depotId, rackId) ? R.okMsg("仓库绑定货架成功") : R.fail("仓库绑定货架失败");
    }

    @ApiOperation("仓库解除绑定货架")
    @PostMapping("/depot/removeBind/rack")
    public R<?> depotRemoveRack(@RequestParam("depotId") String depotId, @RequestParam("rackId") String rackId) {
        DataUtil.checkNulls(depotId, rackId);
        return depotService.removeRack(depotId, rackId) ? R.okMsg("仓库解绑货架成功") : R.fail("仓库解绑货架失败");
    }

}
