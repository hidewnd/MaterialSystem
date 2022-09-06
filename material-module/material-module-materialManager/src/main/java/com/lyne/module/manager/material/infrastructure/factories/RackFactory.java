package com.lyne.module.manager.material.infrastructure.factories;

import cn.hutool.core.convert.Convert;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 构造器
 * 货架实体
 *
 * @author lyne
 */

@Component
public class RackFactory extends AbstractFactory<RackVo, MaterialRack> {
    @Resource
    private FeignMaterialService feignMaterialService;
    @Resource
    private MaterialInfoFactory materialInfoFactory;

    /**
     * 保存实体状态
     *
     * @param rackVo vo
     */
    @Override
    public void save(RackVo rackVo) {
        super.save(rackVo);
    }

    /**
     * 删除实体状态
     *
     * @param rackVo vo
     */
    @Override
    public void delete(RackVo rackVo) {
        super.delete(rackVo);
    }

    /**
     * 获取实体
     *
     * @param t vo
     */
    @Override
    public RackVo get(MaterialRack t) {
        if (t == null || t.getRackId() == null) {
            return null;
        }
        return getById(t.getRackId());
    }

    /**
     * 通过id获取实体
     *
     * @param id id
     */
    @Override
    public RackVo getById(String id) {
        if (id == null) {
            return null;
        }
        RackVo one = MongoDBService.findOneById(RackVo.class, id);
        return one != null ? one : construct(id);
    }

    /**
     * 构造新实体
     *
     * @param rackId rackid
     * @return vo
     */
    private RackVo construct(String rackId) {
        R<?> res = feignMaterialService.queryOne("rack", "id",
                rackId, SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            MaterialRack rack = ParseUtil.parseJson(res.getData(), MaterialRack.class);
            if (rack != null) {
                RackVo rackVo = new RackVo(rack);
                rackVo.setMaterialList(constructList(rackId));
                rackVo.setCapacity(getActualCapacity(rackVo));
                save(rackVo);
                return rackVo;
            }
        }
        return null;
    }

    /**
     * 构造下属关系
     *
     * @param rackId id
     * @return materialList
     */
    public List<String> constructList(String rackId) {
        ArrayList<String> materialInfos = new ArrayList<>();
        R<?> res = feignMaterialService.queryList("rack", "material", "matId",
                Convert.toStr(rackId), SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            List<String> ids = ParseUtil.parseList(res.getData(), String.class);
            if (ids != null) {
                materialInfos.addAll(ids);
            }
        }
        return materialInfos;
    }

    /**
     * 实体转换
     *
     * @param vo vo
     * @return MaterialRack
     */
    public MaterialRack convertRack(RackVo vo) {
        MaterialRack rack = new MaterialRack();
        rack.setRackId(vo.getRackId());
        rack.setDepotId(vo.getDepotId());
        rack.setMaxCapacity(vo.getMaxCapacity());
        rack.setDescription(vo.getDescription());
        rack.setStatus(vo.getStatus());
        rack.setCreateBy(vo.getCreateBy());
        rack.setUpdateBy(vo.getUpdateBy());
        rack.setRemark(vo.getRemark());
        return rack;
    }


    /**
     * 计算货架当前容量
     *
     * @param vo vo
     * @return capacity
     */
    public Long getActualCapacity(RackVo vo) {
        List<String> materialList = vo.getMaterialList();
        // 实际使用容量
        long sum = materialList.stream()
                .map(materialId -> materialInfoFactory.get(vo.getRackId(), materialId))
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .mapToLong(info -> info.getNumber() * info.getCapacityRatio())
                .sum();
        return vo.getMaxCapacity() - sum;
    }


    /**
     * 数据同步
     */
    @Override
    public void syncData() {
        R<?> res = feignMaterialService.queryList("rack", "all", SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            List<MaterialRack> list = ParseUtil.parseList(res.getData(), MaterialRack.class);
            if (list != null) {
                list.forEach(rack -> {
                    RackVo rackVo = new RackVo(rack);
                    List<Material> materialList = rack.getMaterialList();
                    if (materialList != null && materialList.size() > 0) {
                        List<String> collect = materialList.stream()
                                .map(Material::getMaterialId).collect(Collectors.toList());
                        long sum = materialList.stream().mapToLong(Material::getStock).sum();
                        rackVo.setMaterialList(collect);
                        rackVo.setCapacity(rackVo.getMaxCapacity() - sum);
                    }
                    try {
                        MongoDBService.getMongoTemplate().insert(rackVo);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }
}
