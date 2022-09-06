package com.lyne.module.manager.material.interfaces.facade;

import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialType;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialTypeVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.interfaces.dto.MaterialDto;
import com.lyne.module.manager.material.interfaces.dto.TypeDto;
import org.springframework.stereotype.Component;

/**
 * 转换层
 * 用于外界与内部的数据转换
 *
 * @author lyne
 */
@Component
public class MaterialFacade {

    public Material convertMaterial(MaterialDto dto) {
        Material material = new Material();
        if (dto != null) {
            if (dto.getMaterialId() != null) {
                material.setMaterialId(dto.getMaterialId());
            }
            if (dto.getTypeId() != null) {
                material.setMaterialTypeId(dto.getTypeId());
            }
            if (dto.getMaterialName() != null) {
                material.setMaterialName(dto.getMaterialName());
            }
            if (dto.getMaterialNameZh() != null) {
                material.setMaterialNameZh(dto.getMaterialNameZh());
            }
            if (dto.getValue() != null) {
                material.setValue(dto.getValue());
            }
            if (dto.getCapacityRatio() != null) {
                material.setCapacityRatio(dto.getCapacityRatio());
            }
            if (dto.getMessage() != null) {
                material.setMessage(dto.getMessage());
            }
            if (dto.getRemark() != null) {
                material.setRemark(dto.getRemark());
            }
            if (dto.getStatus() != null) {
                material.setStatus(dto.getStatus());
            }
        }
        return material;
    }

    public MaterialType convertType(TypeDto dto) {
        MaterialType type = new MaterialType();
        type.setTypeId(dto.getTypeId());
        if (dto.getParentId() != null) {
            type.setParentId(dto.getParentId());
        }
        if (dto.getTypeName() != null) {
            type.setTypeName(dto.getTypeName());
        }
        if (dto.getTypeNameZh() != null) {
            type.setTypeNameZh(dto.getTypeNameZh());
        }
        if (dto.getRemark() != null) {
            type.setRemark(dto.getRemark());
        }
        if (dto.getStatus() != null) {
            type.setStatus(dto.getStatus());
        }
        return type;
    }

    /**
     * 获取查询类型
     *
     * @param type 入参类型
     * @return 类文件
     */
    public Class<?> getQueryType(String type) {
        if (type == null || type.isEmpty()) {
            return MaterialVo.class;
        }
        switch (type) {
            case "material":
                return MaterialVo.class;
            case "type":
                return MaterialTypeVo.class;
            case "info":
                return MaterialInfo.class;
            default:
                throw new ArgumentException("参数未知错误");
        }
    }
}
