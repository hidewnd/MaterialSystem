package com.lyne.module.manager.material.domain.material.service;

import cn.hutool.core.util.StrUtil;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.manager.material.application.service.MaterialService;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialTypeVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.material.repository.MaterialRepository;
import com.lyne.module.manager.material.domain.material.repository.MaterialTypeRepository;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;
import com.lyne.module.manager.material.infrastructure.constant.ServiceConstant;
import com.lyne.module.manager.material.infrastructure.factories.MaterialFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialInfoFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialTypeFactory;
import com.lyne.module.manager.material.infrastructure.factories.RackFactory;
import com.lyne.module.manager.material.infrastructure.factories.RecordFactory;
import com.lyne.module.manager.material.infrastructure.utils.PageUtil;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;

/**
 * ????????????
 * ??????????????????????????????
 *
 * @author lyne
 */

@Service
public class MaterialServiceImpl implements MaterialService {

    @Resource
    private MaterialInfoFactory materialInfoFactory;
    @Resource
    private MaterialFactory materialFactory;
    @Resource
    private MaterialTypeFactory typeFactory;
    @Resource
    private RecordFactory<RecordVo> recordFactory;
    @Resource
    private MaterialRepository materialRepository;
    @Resource
    private MaterialTypeRepository matTypeRepository;
    @Resource
    private RackFactory rackFactory;

    //==================
    //      ????????????
    //==================

    /**
     * ???????????????????????????????????????
     *
     * @param recordId   ??????ID
     * @param materialId ??????ID
     * @return ????????????
     */
    @Override
    public String getMaterialState(String recordId, String materialId) {
        DataUtil.checkNumbers(recordId, materialId);
        RecordVo instant = recordFactory.getById(recordId);
        Assert.notNull(instant, ServiceConstant.RECORD_NOT_FOUND_RECORD);
        if (instant.getSign() == Registration.ENTER) {
            EnterRecordVo enter = (EnterRecordVo) instant;
            return enter.getRecordStatus();
        } else if (instant.getSign() == Registration.OUTER) {
            return ((OuterRecordVo) instant).getMaterialStatus(materialId);
        }
        return "????????????";
    }

    /**
     * ??????????????????
     *
     * @param rackId     ??????ID
     * @param materialId ??????ID
     * @return ????????????
     */
    @Override
    public String getLocation(String rackId, String recordId, String materialId) {
        DataUtil.checkNulls(rackId, materialId);
        MaterialInfo materialInfo = materialInfoFactory.get(rackId, recordId, materialId);
        return materialInfo.getLocation();
    }

    @Override
    public List<String> getLocation(String rackId, String materialId) {
        DataUtil.checkNulls(rackId, materialId);
        List<MaterialInfo> materialInfo = materialInfoFactory.get(rackId, materialId);
        return materialInfo.stream().map(MaterialInfo::getLocation).collect(Collectors.toList());
    }

    /**
     * ??????????????????
     * ??????????????????
     * @param materialId ??????ID
     * @return ??????
     */
    @Override
    public long queryCapacity(String materialId) {
        DataUtil.checkNumbers(materialId);
        List<MaterialInfo> list = MongoDBService.find(MaterialInfo.class,
                Query.query(Criteria.where("materialId").is(materialId)));
        if (list.size() > 0) {
            // ????????????
            long sum = list.stream().mapToLong(MaterialInfo::getNumber).sum();
            // ????????????
            long temporary = list.stream()
                    .map(MaterialInfo::getRackId)
                    .map(aLong -> rackFactory.getById(aLong))
                    .filter(Objects::nonNull)
                    .map(rack -> rack.getTemporaryQueue().keySet().stream()
                            .filter(qrcode -> MaterialInfo.decodeQR(qrcode)[1].equals(materialId))
                            .map(qrcode -> rack.getTemporaryQueue().get(qrcode)))
                    .flatMap(Stream::sorted)
                    .mapToLong(along -> along)
                    .sum();
            return sum - temporary;
        }
        return 0;
    }

    /**
     * ????????????????????????
     * @return map
     */
    @Override
    public Map<String, Long> queryAllCapacity() {
        List<MaterialVo> materialList = MongoDBService.findAll(MaterialVo.class);
        HashMap<String, Long> map = new HashMap<>();
        if (materialList.size() > 0) {
            materialList.stream()
                    .map(MaterialVo::getMaterialId)
                    .forEach(id -> map.put(id, queryCapacity(id)));
        }
        return map;
    }

    /**
     * ????????????
     * ????????????
     *
     * @param rackId        ??????Id
     * @param recordId      ??????Id
     * @param materialId    ??????Id
     */
    @Override
    public MaterialInfo queryInfo(String rackId, String recordId, String materialId) {
        DataUtil.checkNulls(rackId, materialId);
        recordId = recordId == null ? "1" : recordId;
        return materialInfoFactory.get(rackId, recordId, materialId);
    }


    /**
     * ????????????
     *
     * @param clazz ????????????
     * @param id    ??????id
     * @return t
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T queryOne(Class<T> clazz, String id) {
        if (MaterialVo.class == clazz) {
            return (T) materialFactory.getById(id);
        } else if (MaterialTypeVo.class == clazz) {
            return (T) typeFactory.getById(id);
        }
        return null;
    }

    /**
     * ????????????
     *
     * @param clazz    ????????????
     * @param size     ????????????
     * @param pageSize ??????
     * @param arg1     ???????????????
     * @param arg2     ???????????????
     * @return list
     */
    @Override
    public <T> PageObject<T> queryList(Class<T> clazz, Integer size, Integer pageSize, String arg1, String arg2) {
        if (MaterialVo.class == clazz) {
            materialFactory.syncData();
        } else if (MaterialTypeVo.class == clazz) {
            typeFactory.syncData();
        } else if (MaterialInfo.class == clazz) {
            materialInfoFactory.syncData();
        } else {
            throw new MaterialManagerException("?????????????????????");
        }
        if (StringUtil.hasText(arg1) && StringUtil.hasText(arg2)) {
            if ("materialName".equals(arg1) || "typeName".equals(arg1)) {
                return PageUtil.getPage(MongoDBService.page(clazz, Criteria.where(arg1).regex(".*?" + arg2 + ".*?"), size, pageSize));
            }
            return PageUtil.getPage(MongoDBService.page(clazz, Criteria.where(arg1).is(arg2), size, pageSize));
        }
        return PageUtil.getPage(MongoDBService.page(clazz, size, pageSize));
    }

    /**
     * ???????????????????????????????????????
     * @param materialId ??????id
     * @return list
     */
    @Override
    public List<MaterialInfo> queryInfoList(String materialId) {
        DataUtil.checkNulls(materialId);
        return MongoDBService.find(MaterialInfo.class, Query.query(Criteria.where("materialId").is(materialId)));
    }

    @Override
    public PageObject<MaterialInfo> queryInfoListByArg(String depotId, String rackId, String materialId,
                                                       Integer page, Integer size) {
        PageObject<MaterialInfo> pageObject;
        if (StringUtil.hasText(depotId) || StringUtil.hasText(rackId) || StringUtil.hasText(materialId)) {
            Criteria criteria = null;
            if (StringUtil.hasText(depotId)) {
                criteria = Criteria.where("depotId").is(depotId);
            }
            if (StringUtil.hasText(rackId)) {
                if (criteria != null) {
                    criteria.and("rackId").is(rackId);
                } else {
                    criteria = Criteria.where("rackId").is(rackId);
                }
            }
            if (StringUtil.hasText(materialId)) {
                if (criteria != null) {
                    criteria.and("materialId").is(materialId);
                } else {
                    criteria = Criteria.where("materialId").is(materialId);
                }
            }
            pageObject = PageUtil.getPage(MongoDBService.page(MaterialInfo.class, criteria, size, page));
        } else {
            pageObject = PageUtil.getPage(MongoDBService.page(MaterialInfo.class, size, page));
        }
        List<MaterialInfo> element = pageObject.getElement();
        if (element.size() > 0) {
            for (MaterialInfo info : element) {
                DepotVo depotVo = MongoDBService.findOneById(DepotVo.class, info.getDepotId());
                if (depotVo != null) {
                    info.setDepotName(depotVo.getDepotName());
                }
                MaterialVo materialVo = MongoDBService.findOneById(MaterialVo.class, info.getMaterialId());
                if (materialVo != null) {
                    info.setMaterialName(materialVo.getMaterialName());
                    info.setMaterialNameZh(materialVo.getMaterialNameZh());
                }
            }
            pageObject.setElement(element);
        }
        return pageObject;
    }

    //==================
    //      ????????????
    //==================

    /**
     * ????????????
     * @param material ????????????
     * @return materialId
     */
    @Override
    public String generalMaterial(Material material) {
        DataUtil.checkNulls(material);
        MaterialVo materialVo = materialRepository.insertSync(material);
        return materialVo != null ? materialVo.getMaterialId() : null;
    }

    /**
     * ??????????????????
     * @param type ????????????
     * @return typeId
     */
    @Override
    public String generalType(MaterialType type) {
        DataUtil.checkNulls(type);
        MaterialTypeVo materialTypeVo = matTypeRepository.insertSync(type);
        return materialTypeVo != null ? materialTypeVo.getTypeId() : null;
    }

    /**
     * ??????????????????
     * @param material  ????????????
     * @return boolean
     */
    @Override
    public boolean updateMaterial(Material material, String operator) {
        DataUtil.checkNulls(material, material.getMaterialId());
        MaterialVo materialVo = materialFactory.getById(material.getMaterialId());
        Assert.notNull(materialVo, ServiceConstant.MATERIAL_NOT_FOUND);
        if (material.getMaterialName() != null) {
            materialVo.setMaterialName(material.getMaterialName());
        }
        if (material.getMaterialTypeId() != null) {
            materialVo.setTypeId(material.getMaterialTypeId());
        }
        if (material.getValue() != null) {
            materialVo.setValue(material.getValue());
        }
        if (material.getMaterialNameZh() != null) {
            materialVo.setMaterialNameZh(material.getMaterialNameZh());
        }
        if (material.getCapacityRatio() != null) {
            materialVo.setCapacityRatio(material.getCapacityRatio());
        }
        if (material.getMessage() != null) {
            materialVo.setMessage(material.getMessage());
        }
        if (material.getRemark() != null) {
            materialVo.setRemark(material.getRemark());
        }
        if (material.getStatus() != null) {
            materialVo.setStatus(material.getStatus());
        }
        materialVo.setUpdateBy(operator);
        materialFactory.save(materialVo);
        return true;
    }

    /**
     * ????????????????????????
     * @param type      ??????????????????
     * @return boolean
     */
    @Override
    public boolean updateMaterialType(MaterialType type, String operator) {
        DataUtil.checkNulls(type, type.getTypeId());
        MaterialTypeVo typeVo = typeFactory.getById(type.getTypeId());
        Assert.notNull(typeVo, ServiceConstant.MATERIAL_TYPE_NOT_FOUND);
        if (type.getParentId() != null && !StrUtil.isBlank(type.getParentId())) {
            typeVo.setParentId(type.getParentId());
        }
        if (type.getTypeName() != null && !StrUtil.isBlank(type.getTypeName())) {
            typeVo.setTypeName(type.getTypeName());
        }
        if (type.getTypeNameZh() != null && !StrUtil.isBlank(type.getTypeNameZh())) {
            typeVo.setTypeNameZh(type.getTypeNameZh());
        }
        if (type.getRemark() != null) {
            typeVo.setRemark(type.getRemark());
        }
        if (type.getStatus() != null) {
            typeVo.setStatus(type.getStatus());
        }
        typeVo.setUpdateBy(operator);
        typeFactory.save(typeVo);
        return true;
    }

    /**
     * ??????????????????
     * @param materialId    ??????ID
     * @param operator      ????????????
     * @return boolean
     */
    @Override
    public boolean deleteMaterial(String materialId, String operator) {
        DataUtil.checkNumbers(materialId);
        Assert.notNull(operator, "????????????????????????");
        MaterialVo materialVo = materialFactory.getById(materialId);
        Assert.notNull(materialVo, ServiceConstant.MATERIAL_NOT_FOUND);
        if (materialInfoFactory.get(materialId).size() > 0) {
            throw new MaterialManagerException("????????????????????????????????????");
        }
        materialVo.setUpdateBy(operator);
        materialRepository.save(materialVo);
        materialRepository.delete(materialVo);
        return true;
    }

    /**
     * ????????????????????????
     * @param typeId        ????????????ID
     * @param operator      ????????????
     * @return boolean
     */
    @Override
    public boolean deleteMaterialType(String typeId, String operator) {
        DataUtil.checkNumbers(typeId);
        Assert.notNull(operator, "????????????????????????");
        MaterialTypeVo typeVo = typeFactory.getById(typeId);
        Assert.notNull(typeVo, ServiceConstant.MATERIAL_TYPE_NOT_FOUND);
        if ("1".equals(typeId)) {
            throw new MaterialManagerException("??????????????????????????????");
        }
        List<MaterialVo> materialVos = MongoDBService.find(MaterialVo.class, Query.query(Criteria
                .where("typeId").is("typeId")));
        if (materialVos.size() > 0) {
            for (MaterialVo materialVo : materialVos) {
                materialVo.setTypeId("1");
                materialRepository.save(materialVo);
            }
        }
        typeVo.setUpdateBy(operator);
        matTypeRepository.save(typeVo);
        matTypeRepository.delete(typeVo);
        return true;
    }
}
