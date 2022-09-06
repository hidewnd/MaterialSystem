package com.lyne.module.manager.material.infrastructure.factories;

import cn.hutool.core.convert.Convert;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.annotation.Resource;

/**
 * 领域工厂
 * 物料
 *
 * @author lyne
 */

@Component
public class MaterialFactory extends AbstractFactory<MaterialVo, Material> {

    @Resource
    private FeignMaterialService feignMaterialService;

    @Override
    public void save(MaterialVo materialVo) {
        materialVo.dataCheck();
        MongoDBService.save(materialVo);
    }

    @Override
    public void delete(MaterialVo materialVo) {
        MongoDBService.remove(materialVo);
    }

    /**
     * 构造领域对象
     *
     * @param t t
     */
    @Override
    public MaterialVo get(Material t) {
        if (t == null || t.getMaterialId() != null) {
            return null;
        }
        return getById(t.getMaterialId());
    }

    @Override
    public MaterialVo getById(String id) {
        if (id == null) {
            return null;
        }
        MaterialVo one = MongoDBService.findOneById(MaterialVo.class, id);
        return one != null ? one : construct(id);
    }

    /**
     * 构造新的领域实体
     *
     * @param materialId 物料ID
     * @return MaterialVo
     */
    public MaterialVo construct(String materialId) {
        R<?> r = feignMaterialService.queryOne("material", "id",
                Convert.toStr(materialId), SecurityConstants.INNER);
        if (r != null && r.getCode() == HttpStatus.SUCCESS) {
            Material material = ParseUtil.parseJson(r.getData(), Material.class);
            if (material != null) {
                MaterialVo vo = convert(material);
                save(vo);
                return vo;
            }
        }
        return null;
    }

    private MaterialVo convert(Material material) {
        if (material == null) {
            return null;
        }
        MaterialVo vo = new MaterialVo();
        vo.setMaterialId(material.getMaterialId());
        vo.setTypeId(material.getMaterialTypeId());
        vo.setMaterialName(material.getMaterialName());
        vo.setMaterialNameZh(material.getMaterialNameZh());
        vo.setValue(material.getValue());
        vo.setCapacityRatio(material.getCapacityRatio());
        vo.setMessage(material.getMessage());
        vo.setStatus(material.getStatus());
        vo.setRemark(material.getRemark());
        vo.setCreateBy(material.getCreateBy());
        vo.setUpdateBy(material.getUpdateBy());
        vo.setRemark(material.getRemark());
        return vo;
    }


    public Material convertMaterial(MaterialVo vo) {
        vo.dataCheck();
        Material material = new Material();
        material.setMaterialId(vo.getMaterialId());
        material.setMaterialName(vo.getMaterialName());
        material.setMaterialNameZh(vo.getMaterialNameZh());
        if (vo.getTypeId() != null) {
            material.setMaterialTypeId(vo.getTypeId());
        }
        material.setValue(vo.getValue());
        material.setCapacityRatio(vo.getCapacityRatio());
        material.setMessage(vo.getMessage());
        material.setStatus(vo.getStatus());
        material.setCreateBy(vo.getCreateBy());
        material.setUpdateBy(vo.getUpdateBy());
        material.setRemark(vo.getRemark());
        return material;
    }

    @Override
    public void syncData() {
        R<?> res = feignMaterialService.queryList("material", "all", SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            List<Material> list = ParseUtil.parseList(res.getData(), Material.class);
            long total = MongoDBService.getMongoTemplate().estimatedCount(MaterialVo.class);
            if (list != null && list.size() > total) {
                list.forEach(material -> {
                    if (MongoDBService.findOneById(MaterialVo.class, material.getMaterialId()) == null) {
                        MaterialVo convert = convert(material);
                        try {
                            MongoDBService.getMongoTemplate().insert(convert);
                        } catch (Exception ignored) {
                        }
                    }
                });
            }
        }
    }
}
