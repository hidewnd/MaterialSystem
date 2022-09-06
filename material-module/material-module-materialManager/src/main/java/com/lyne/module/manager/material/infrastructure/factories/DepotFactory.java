package com.lyne.module.manager.material.infrastructure.factories;

import cn.hutool.core.convert.Convert;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 领域实体构造器
 * 仓库
 *
 * @author lyne
 */

@Component
public class DepotFactory extends AbstractFactory<DepotVo, MaterialDepot> {
    @Resource
    private FeignMaterialService feignMaterialService;

    @Override
    public void save(DepotVo depotVo) {
        MongoDBService.save(depotVo);
    }

    @Override
    public void delete(DepotVo depotVo) {
        MongoDBService.remove(depotVo);
    }

    @Override
    public DepotVo get(MaterialDepot t) {
        if (t == null || t.getDepotId() == null) {
            return null;
        }
        return getById(t.getDepotId());
    }

    @Override
    public DepotVo getById(String id) {
        if (id == null) {
            return null;
        }
        DepotVo one = MongoDBService.findOneById(DepotVo.class, id);
        return one == null ? construct(id) : one;
    }

    /**
     * 构造实体
     *
     * @param depotId id
     * @return vo
     */
    private DepotVo construct(String depotId) {
        R<?> res = feignMaterialService.queryOne("depot", "id", Convert.toStr(depotId),
                SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            MaterialDepot depot = ParseUtil.parseJson(res.getData(), MaterialDepot.class);
            if (depot != null) {
                DepotVo depotVo = new DepotVo(depot);
                List<MaterialRack> rackList = depot.getRackList();
                if (rackList != null && rackList.size() > 0) {
                    List<String> collect = rackList.stream().map(MaterialRack::getRackId).collect(Collectors.toList());
                    depotVo.setRackList(collect);
                } else {
                    depotVo.setRackList(constructList(depotId));
                }
                save(depotVo);
                return depotVo;
            }
        }
        return null;
    }

    /**
     * 构造下属关系
     *
     * @param depotId depotId
     * @return list
     */
    public List<String> constructList(String depotId) {
        ArrayList<String> rackVos = new ArrayList<>();
        R<?> res = feignMaterialService.queryList("rack", "eq", "depot_id",
                Convert.toStr(depotId), SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            List<MaterialRack> list = ParseUtil.parseList(res.getData(), MaterialRack.class);
            if (list != null && list.size() > 0) {
                List<String> collect = list.stream()
                        .map(MaterialRack::getRackId)
                        .collect(Collectors.toList());
                rackVos.addAll(collect);
            }
        }
        return rackVos;
    }

    /**
     * 实体转换
     *
     * @param vo vo
     * @return MaterialDepot
     */
    public MaterialDepot convertDepot(DepotVo vo) {
        MaterialDepot depot = new MaterialDepot();
        depot.setDepotId(vo.getDepotId());
        depot.setDepotName(vo.getDepotName());
        depot.setDepotNameZh(vo.getDepotNameZh());
        depot.setPlace(vo.getPlace());
        depot.setMaxCapacity(vo.getMaxCapacity());
        depot.setDescription(vo.getDescription());
        depot.setStatus(vo.getStatus());
        depot.setCreateBy(vo.getCreateBy());
        depot.setUpdateBy(vo.getUpdateBy());
        depot.setRemark(vo.getRemark());
        depot.setThreshold(vo.getThreshold());
        return depot;
    }

    /**
     * 数据同步
     */
    @Override
    public void syncData() {
        R<?> res = feignMaterialService.queryList("depot", "all", SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            List<MaterialDepot> list = ParseUtil.parseList(res.getData(), MaterialDepot.class);
            if (list != null) {
                list.forEach(materialDepot -> {
                    DepotVo depotVo = new DepotVo(materialDepot);
                    if (materialDepot.getRackList() != null) {
                        List<String> collect = materialDepot.getRackList().stream()
                                .map(MaterialRack::getRackId)
                                .collect(Collectors.toList());
                        depotVo.setRackList(collect);
                    }
                    try {
                        MongoDBService.getMongoTemplate().insert(depotVo);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }
}
