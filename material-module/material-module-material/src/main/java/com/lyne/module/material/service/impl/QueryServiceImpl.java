package com.lyne.module.material.service.impl;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lyne.common.core.base.R;
import com.lyne.common.core.exception.ArgumentException;
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
import com.lyne.module.material.service.QueryService;
import com.lyne.module.material.service.RackService;
import com.lyne.module.material.service.RegistrationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @author lyne
 */
@Service
public class QueryServiceImpl implements QueryService {
    @Resource
    private DepotService depotService;
    @Resource
    private RackService rackService;
    @Resource
    private RegistrationService registrationService;
    @Resource
    private MaterialTypeService typeService;
    @Resource
    private MaterialService materialService;


    @Override
    public R<?> queryOne(String name, String type, String arg1, String arg2) {
        switch (name) {
            case DEPOT:
                return queryDepotOne(type, arg1, arg2);
            case RACK:
                return queryRackOne(type, arg1, arg2);
            case MATERIAL:
                return queryMaterialOne(type, arg1, arg2);
            case MATERIAL_TYPE:
                return queryMaterialTypeOne(type, arg1, arg2);
            case RECORD:
                return queryRegistrationOne(type, arg1, arg2);

            default:
                return R.fail("未知的数据类型");
        }
    }

    @Override
    public R<?> queryList(String name, String type, String column, String arg1, String arg2) {
        switch (name) {
            case DEPOT:
                Wrapper<MaterialDepot> depotWrapper = queryListWrapper(type, column, arg1, arg2);
                return R.ok("查询仓库信息列表成功", depotService.queryList(depotWrapper));
            case RACK:
                if (MATERIAL.equals(type)) {
                    DataUtil.checkNulls(arg1);
                    if (FLAG_ALL.equals(arg1)) {
                        return R.ok("查询货架信息列表成功", rackService.getAllMaterial());
                    }
                    if (FLAG_MAT_ID.equals(column)) {
                        return R.ok("查询货架信息列表成功", rackService.getMaterialIds(arg1));
                    }
                    return R.ok("查询货架信息列表成功", rackService.getMaterialList(arg1));
                }
                Wrapper<MaterialRack> rackWrapper = queryListWrapper(type, column, arg1, arg2);
                return R.ok("查询货架信息列表成功", rackService.queryList(rackWrapper));
            case MATERIAL:
                Wrapper<Material> materialWrapper = queryListWrapper(type, column, arg1, arg2);
                return R.ok("查询物料信息列表成功", materialService.list(materialWrapper));
            case MATERIAL_TYPE:
                Wrapper<MaterialType> typeWrapper = queryListWrapper(type, column, arg1, arg2);
                return R.ok("查询物料类型信息列表成功", typeService.list(typeWrapper));
            case RECORD:
                // 查询记录所包含物料
                if (MATERIAL.equals(type)) {
                    DataUtil.checkNulls(arg1);
                    if (FLAG_IDS.equals(column)) {
                        return R.ok("查询出入库记录信息列表成功", registrationService.getMaterialIds(arg1));
                    }
                    return R.ok("查询出入库记录信息列表成功", registrationService.getMaterialList(arg1));
                }
                Wrapper<Registration> registrationWrapper = queryListWrapper(type, column, arg1, arg2);
                return R.ok("查询出入库记录信息列表成功", registrationService.list(registrationWrapper));
            default:
                return R.fail("未知类型");
        }
    }

    @Override
    public R<?> queryPage(String name, Integer size, Integer page) {
        page = page == null ? 1 : page;
        size = size == null ? 10 : size;
        switch (name) {
            case DEPOT:
                return R.ok("分页查询仓库列表成功", depotService.queryByPage(page, size));
            case RACK:
                return R.ok("分页查询货架列表成功", rackService.queryByPage(page, size));
            case MATERIAL:
                return R.ok("分页查询物料列表成功", materialService.queryByPage(page, size));
            case MATERIAL_TYPE:
                return R.ok("分页查询物料类型列表成功", typeService.queryByPage(page, size));
            case RECORD:
                return R.ok("分页查询出入库记录信息列表成功", registrationService.queryByPage(page, size));
            default:
                return R.fail("未知类型");
        }
    }


    /**
     * 单数据查询仓库
     * @param way       查询方式
     * @param arg1      参数一
     * @param arg2      参数二
     * @return R
     */
    private R<MaterialDepot> queryDepotOne(String way, String arg1, String arg2) {
        DataUtil.checkNulls(way, arg1);
        QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
        switch (way) {
            case "id":
                return R.ok("查询仓库信息成功", depotService.getById(arg1));
            case "name":
                wrapper.eq("depot_name", arg1);
                return R.ok("查询仓库信息成功", depotService.getOne(wrapper));
            case "column":
                DataUtil.checkNulls(arg1, arg2);
                wrapper.eq(arg1, arg2);
                return R.ok("查询仓库信息成功", depotService.getOne(wrapper));
            default:
                return R.fail("未知参数");
        }
    }

    /**
     * 单数据查询货架
     * @param way       查询方式
     * @param arg1      参数一
     * @param arg2      参数二
     * @return R
     */
    private R<MaterialRack> queryRackOne(String way, String arg1, String arg2) {
        DataUtil.checkNulls(way, arg1);
        QueryWrapper<MaterialRack> wrapper = new QueryWrapper<>();
        switch (way) {
            case DEPOT:
                DataUtil.checkNulls(arg2);
                MaterialRack rack = depotService.getRackById(arg1, arg2);
                return R.ok("查询货架信息成功", rack);
            case "id":
                return R.ok("查询货架信息成功", rackService.getById(arg1));
            case "column":
                DataUtil.checkNulls(arg2);
                wrapper.eq(arg1, arg2);
                return R.ok("查询货架信息成功", rackService.getOne(wrapper));
            default:
                return R.fail("未知参数");
        }
    }

    /**
     * 单数据查询物料
     * @param way       查询方式
     * @param arg1      参数一
     * @param arg2      参数二
     * @return R
     */
    private R<Material> queryMaterialOne(String way, String arg1, String arg2) {
        DataUtil.checkNulls(way, arg1);
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        switch (way) {
            case "id":
                return R.ok("查询物料信息成功", materialService.getById(arg1));
            case "name":
                wrapper.eq("material_name", arg1);
                return R.ok("查询物料信息成功", materialService.getOne(wrapper));
            case "column":
                DataUtil.checkNulls(arg2);
                wrapper.eq(arg1, arg2);
                return R.ok("查询物料信息成功", materialService.getOne(wrapper));
            default:
                return R.fail("未知参数");
        }
    }

    /**
     * 单数据查询物料类型
     * @param way       查询方式
     * @param arg1      参数一
     * @param arg2      参数二
     * @return R
     */
    private R<MaterialType> queryMaterialTypeOne(String way, String arg1, String arg2) {
        DataUtil.checkNulls(way, arg1);
        QueryWrapper<MaterialType> wrapper = new QueryWrapper<>();
        switch (way) {
            case "id":
                return R.ok("查询物料类型信息成功", typeService.getById(arg1));
            case "parent":
                wrapper.eq("parent_id", Convert.toInt(arg1));
                return R.ok("查询物料类型信息成功", typeService.getById(arg1));
            case "name":
                return R.ok("查询物料类型信息成功", typeService.queryByName(arg1));
            case "column":
                DataUtil.checkNulls(arg2);
                wrapper.eq(arg1, arg2);
                return R.ok("查询物料类型信息成功", typeService.getOne(wrapper));
            default:
                return R.fail("未知参数");
        }
    }

    /**
     * 单数据查询出入库记录
     * @param way       查询方式
     * @param arg1      参数一
     * @param arg2      参数二
     * @return R
     */
    private R<?> queryRegistrationOne(String way, String arg1, String arg2) {
        DataUtil.checkNulls(way, arg1);
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        switch (way) {
            case "id":
                return R.ok("查询出入库记录信息成功", registrationService.getById(arg1));
            case "column":
                DataUtil.checkNulls(arg2);
                wrapper.eq(arg1, arg2);
                return R.ok("查询出入库记录信息成功", registrationService.getOne(wrapper));
            case MATERIAL:
                DataUtil.checkNulls(arg2);
                RegMaterial material = registrationService
                        .getMaterial(arg1, arg2);
                return R.ok("查询出入库记录信息成功", material);
            default:
                return R.fail("未知参数");
        }
    }

    /**
     * 查询条件构造器
     * @param way           查询方式
     * @param column        查询表字段
     * @param variable1     参数一
     * @param variable2     参数二
     * @param <T>           实体类型
     * @return wrapper
     */
    private <T> Wrapper<T> queryListWrapper(String way, String column, String variable1, String variable2) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (FLAG_ALL.equals(way)) {
            return wrapper;
        }
        DataUtil.checkNulls(column, variable1);
        switch (way) {
            case "eq":
                wrapper.eq(column, variable1);
                break;
            case "gt":
                wrapper.gt(column, variable1);
                break;
            case "ge":
                wrapper.ge(column, variable1);
                break;
            case "lt":
                wrapper.lt(column, variable1);
                break;
            case "le":
                wrapper.le(column, variable1);
                break;
            case "bet":
                DataUtil.checkNulls(variable2);
                wrapper.between(column, variable1, variable2);
                break;
            case "like":
                wrapper.like(column, variable1);
                break;
            case "notBet":
                DataUtil.checkNulls(variable2);
                wrapper.notBetween(column, variable1, variable2);
                break;
            default:
                throw new ArgumentException("未知参数");
        }
        return wrapper;
    }

    private static final String MATERIAL = "material";
    private static final String DEPOT = "depot";
    private static final String RACK = "rack";
    private static final String MATERIAL_TYPE = "matType";
    private static final String RECORD = "registration";
    private static final String FLAG_ALL = "all";
    private static final String FLAG_MAT_ID = "matId";
    private static final String FLAG_IDS = "ids";

}
