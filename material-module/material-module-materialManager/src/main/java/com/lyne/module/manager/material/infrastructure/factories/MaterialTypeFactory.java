package com.lyne.module.manager.material.infrastructure.factories;

import cn.hutool.core.convert.Convert;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialTypeVo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

/**
 * 领域工厂
 *
 * @author lyne
 */
@Component
public class MaterialTypeFactory extends AbstractFactory<MaterialTypeVo, MaterialType> {

    @Resource
    private FeignMaterialService feignMaterialService;

    @Override
    public void save(MaterialTypeVo vo) {
        vo.dataCheck();
        super.save(vo);
    }

    @Override
    public MaterialTypeVo get(MaterialType t) {
        if (t == null) {
            return null;
        }
        return getById(t.getTypeId());
    }

    @Override
    public MaterialTypeVo getById(String id) {
        if (id == null) {
            return null;
        }
        MaterialTypeVo one = MongoDBService.findOneById(MaterialTypeVo.class, id);
        return one != null ? one : construct(id);
    }

    @Override
    public void syncData() {
        R<?> res = feignMaterialService.queryList("matType", "all", SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            List<MaterialType> list = ParseUtil.parseList(res.getData(), MaterialType.class);
            long total = MongoDBService.getMongoTemplate().estimatedCount(MaterialTypeVo.class);
            if (list != null && list.size() > total) {
                list.forEach(type -> {
                    MaterialTypeVo convert = convert(type);
                    try {
                        MongoDBService.getMongoTemplate().insert(convert);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }

    private MaterialTypeVo construct(String typeId) {
        R<?> res = feignMaterialService.queryOne("matType", "id",
                Convert.toStr(typeId), SecurityConstants.INNER);
        if (res.getCode() == HttpStatus.SUCCESS) {
            MaterialType type = ParseUtil.parseJson(res.getData(), MaterialType.class);
            if (type != null) {
                MaterialTypeVo vo = convert(type);
                save(vo);
                return vo;
            }
        }
        return null;
    }

    @Override
    public void delete(MaterialTypeVo vo) {
        MongoDBService.remove(vo);
    }


    public MaterialType convertType(MaterialTypeVo vo) {
        vo.dataCheck();
        MaterialType type = new MaterialType();
        type.setTypeId(vo.getTypeId());
        type.setTypeName(vo.getTypeName());
        type.setTypeNameZh(vo.getTypeNameZh());
        if (vo.getParentId() != null) {
            type.setParentId(vo.getParentId());
        }
        type.setStatus(vo.getStatus());
        type.setCreateBy(vo.getCreateBy());
        type.setUpdateBy(vo.getUpdateBy());
        type.setRemark(vo.getRemark());
        return type;
    }

    private MaterialTypeVo convert(MaterialType type) {
        if (type == null) {
            return null;
        }
        MaterialTypeVo vo = new MaterialTypeVo();
        vo.setTypeId(type.getTypeId());
        vo.setTypeName(type.getTypeName());
        vo.setTypeNameZh(type.getTypeNameZh());
        vo.setParentId(type.getParentId());
        vo.setStatus(type.getStatus());
        vo.setCreateBy(type.getCreateBy());
        vo.setUpdateBy(type.getUpdateBy());
        vo.setRemark(type.getRemark());
        return vo;
    }
}
