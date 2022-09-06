package com.lyne.module.manager.material.infrastructure.factories;

import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.annotation.Resource;

/**
 * 领域实体构造工厂
 * 物料详情
 *
 * @author lyne
 */
@Component
public class MaterialInfoFactory extends AbstractFactory<MaterialInfo, RegMaterial> {

    @Resource
    private RackFactory rackFactory;
    @Resource
    private DepotFactory depotFactory;

    @Resource
    private FeignMaterialService feignMaterialService;

    /**
     * 实体数据保存
     * <p>非持久化操作</p>
     * @param materialInfo 物料详情
     */
    @Override
    public void save(MaterialInfo materialInfo) {
        MongoDBService.save(materialInfo);
    }

    /**
     * 删除实体
     *
     * @param materialInfo info
     */
    @Override
    public void delete(MaterialInfo materialInfo) {
        MongoDBService.remove(materialInfo);
    }

    /**
     * 获取实体
     *
     * @param t 物料详情
     * @return MaterialInfo
     */
    @Override
    public MaterialInfo get(RegMaterial t) {
        if (t == null || t.getMaterial() == null) {
            return null;
        }
        return get(t.getRackId(), t.getRegistrationId(), t.getMaterial().getMaterialId());
    }

    /**
     * 获取实体
     *
     * @param rackId            货架id
     * @param enterRecordId     入库Id
     * @param materialId        物料id
     * @return MaterialInfo
     */
    public MaterialInfo get(String rackId, String enterRecordId, String materialId) {
        if (rackId == null || materialId == null) {
            return null;
        }
        MaterialInfo one = MongoDBService.findOne(MaterialInfo.class, Query.query(Criteria.where("rackId").is(rackId)
                .and("enterRecordId").is(enterRecordId).and("materialId").is(materialId)));
        if (one == null) {
            one = constructor(rackId, enterRecordId, materialId);
        }
        return one;
    }

    public List<MaterialInfo> get(String rackId, String materialId) {
        if (rackId == null || materialId == null) {
            return null;
        }
        return MongoDBService.find(MaterialInfo.class, Query.query(Criteria.where("rackId").is(rackId)
                .and("materialId").is(materialId)));
    }

    public List<MaterialInfo> get(String materialId) {
        if (materialId == null) {
            return null;
        }
        return MongoDBService.find(MaterialInfo.class, Query.query(Criteria.where("materialId").is(materialId)));
    }

    public MaterialInfo getByQrCode(String qrcode) {
        DataUtil.checkNulls(qrcode);
        String[] datas = MaterialInfo.decodeQR(qrcode);
        DataUtil.checkNulls(datas[0], datas[1], datas[2]);
        return get(datas[2], datas[0], datas[1]);
    }


    /**
     * 通过通用id获取物料详情
     *
     * @param id 实体主键
     * @return materialInfo
     */
    @Override
    public MaterialInfo getById(String id) {
        if (id == null) {
            return null;
        }
        return MongoDBService.findOneById(MaterialInfo.class, id);
    }


    /**
     * 构造新实体
     *
     * @param rackId     货架id
     * @param materialId 物料id
     */
    private MaterialInfo constructor(String rackId, String recordId, String materialId) {
        R<?> r = feignMaterialService.queryStockByOne(rackId, recordId, materialId, SecurityConstants.INNER);
        if (r.getCode() == HttpStatus.SUCCESS) {
            Material material = ParseUtil.parseJson(r.getData(), Material.class);
            if (material != null) {
                MaterialInfo info = convert(material, rackId);
                save(info);
                return info;
            }
        }
        return null;
    }

    /**
     * 实体转换
     *
     * @param material 物料实体
     * @param rackId   货架id
     * @return info
     */
    private MaterialInfo convert(Material material, String rackId) {
        MaterialInfo materialInfo = new MaterialInfo(material, rackId);
        RackVo rackVo = rackFactory.getById(rackId);
        if (rackVo != null) {
            DepotVo depotVo = depotFactory.getById(rackVo.getDepotId());
            if (depotVo != null) {
                materialInfo.setDepotId(depotVo.getDepotId());
                materialInfo.setLocation(depotVo.getDepotName(), depotVo.getPlace());
            }
        }
        return materialInfo;
    }

    /**
     * 实体转换
     *
     * @param info 物料详情
     * @return material
     */
    public Material convertToMaterial(MaterialInfo info) {
        Material material = new Material();
        material.setMaterialId(info.getMaterialId());
        material.setRecordId(info.getEnterRecordId());
        material.setRackId(info.getRackId());
        material.setStock(info.getNumber());
        material.setCapacityRatio(info.getCapacityRatio());
        return material;
    }

    /**
     * 添加物料-货架关系
     *
     * @param rackId 货架id
     * @param matId  物料id
     */
    public MaterialInfo insertBind(String rackId, String recordId, String matId, Long num) {
        R<?> res = feignMaterialService.rackStore(rackId, recordId, matId, num, SecurityConstants.INNER);
        if (res != null) {
            if (res.getCode() == HttpStatus.SUCCESS) {
                return constructor(rackId, recordId, matId);
            }
            throw new MaterialManagerException(res.getMsg());
        }
        return null;
    }

    /**
     * 数据同步
     */
    @Override
    public void syncData() {
        List<RackVo> list = MongoDBService.findAll(RackVo.class);
        if (list.size() > 0) {
            list.stream().map(RackVo::getRackId).forEach(rackId -> {
                R<?> res = feignMaterialService.queryStockByList(rackId, null, null,
                        SecurityConstants.INNER);
                if (res != null && res.getCode() == HttpStatus.SUCCESS) {
                    List<Material> materialList = ParseUtil.parseList(res.getData(), Material.class);
                    if (materialList != null && materialList.size() > 0) {
                        materialList.forEach(material -> {
                            MaterialInfo convert = convert(material, rackId);
                            try {
                                MongoDBService.getMongoTemplate().insert(convert);
                            } catch (Exception ignored) {
                            }
                        });
                    }
                }
            });
        }

    }

}
