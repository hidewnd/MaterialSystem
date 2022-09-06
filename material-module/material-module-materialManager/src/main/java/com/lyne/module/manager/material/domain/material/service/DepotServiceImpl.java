package com.lyne.module.manager.material.domain.material.service;

import cn.hutool.core.util.StrUtil;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.exception.ArgumentException;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.manager.material.application.service.DepotService;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.domain.material.repository.DepotRepository;
import com.lyne.module.manager.material.domain.material.repository.RackRepository;
import com.lyne.module.manager.material.infrastructure.constant.ServiceConstant;
import com.lyne.module.manager.material.infrastructure.factories.DepotFactory;
import com.lyne.module.manager.material.infrastructure.factories.RackFactory;
import com.lyne.module.manager.material.infrastructure.utils.PageUtil;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * @author lyne
 */
@Service
public class DepotServiceImpl implements DepotService {
    @Resource
    private DepotRepository depotRepository;
    @Resource
    private RackRepository rackRepository;
    @Resource
    private DepotFactory depotFactory;
    @Resource
    private RackFactory rackFactory;

    /**
     * 新增仓库
     *
     * @param depot 初始信息
     * @return depotId
     */
    @Override
    public String generate(MaterialDepot depot, String operator) {
        if (depot == null) {
            throw new ArgumentException("null值异常");
        }
        depot.setCreateBy(operator);
        depot.setUpdateBy(operator);
        String id = depotRepository.insertSync(depot);
        if (id != null) {
            DepotVo vo = depotFactory.getById(id);
            depotFactory.save(vo);
            return id;
        }
        return null;
    }

    /**
     * 修改货架
     * @param depot         仓库实体
     * @param operator      操作员
     * @return boolean
     */
    @Override
    public boolean updateDepot(MaterialDepot depot, String operator) {
        DataUtil.checkNulls(depot, depot.getDepotId());
        DepotVo depotVo = depotFactory.getById(depot.getDepotId());
        Assert.notNull(depotVo, ServiceConstant.DEPOT_NOT_FOUND);
        depotVo.setUpdateBy(StrUtil.isBlank(operator) ? "system" : operator);
        if (depot.getDepotName() != null) {
            depotVo.setDepotName(depot.getDepotName());
        }
        if (depot.getDepotNameZh() != null) {
            depotVo.setDepotNameZh(depot.getDepotNameZh());
        }
        if (depot.getMaxCapacity() != null) {
            depotVo.setMaxCapacity(depot.getMaxCapacity());
        }
        if (depot.getPlace() != null) {
            depotVo.setPlace(depot.getPlace());
        }
        if (depot.getDescription() != null) {
            depotVo.setDescription(depot.getDescription());
        }
        if (depot.getThreshold() != null) {
            depotVo.setThreshold(depot.getThreshold());
        }
        if (depot.getStatus() != null) {
            depotVo.setStatus(depot.getStatus());
        }
        if (depot.getRemark() != null) {
            depotVo.setRemark(depot.getRemark());
        }
        depotRepository.save(depotVo);
        if (depot.getPlace() != null) {
            UpdateInfoLocation(depot);
        }
        return true;
    }

    public void UpdateInfoLocation(MaterialDepot depot) {
        List<MaterialInfo> list = MongoDBService.find(MaterialInfo.class,
                Query.query(Criteria.where("depotId").is(depot.getDepotId())));
        if (list.size() > 0) {
            for (MaterialInfo info : list) {
                info.setLocation(depot.getDepotName(), depot.getPlace());
                MongoDBService.save(info);
            }
        }
    }

    /**
     * 修改货架
     * @param depotId       仓库Id
     * @param operator      操作员
     * @return boolean
     */
    @Override
    public boolean deleteDepot(String depotId, String operator) {
        Assert.notNull(operator, "操作人员不能为空");
        DataUtil.checkNumbers(depotId);
        DepotVo depotVo = depotFactory.getById(depotId);
        Assert.notNull(depotVo, ServiceConstant.DEPOT_NOT_FOUND);
        int rackVoListSize = MongoDBService.find(RackVo.class, Query.query(Criteria.where("depotId").is(depotId))).size();
        if (rackVoListSize > 0) {
            throw new MaterialManagerException("请提前转移当前仓库下的货架");
        }
        depotVo.setUpdateBy(operator);
        depotRepository.save(depotVo);
        depotRepository.delete(depotVo);
        return true;
    }

    /**
     * 转移货架
     *
     * @param oldId  原有仓库Id
     * @param newId  目标仓库Id
     * @param rackId 货架Id
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferRack(String oldId, String newId, String rackId, String operator) {
        DepotVo vo = depotFactory.getById(oldId);
        Assert.notNull(vo, ServiceConstant.DEPOT_NOT_FOUND);
        vo.setUpdateBy(operator);
        DepotVo target = depotFactory.getById(newId);
        Assert.notNull(target, ServiceConstant.DEPOT_NOT_FOUND);
        target.setUpdateBy(operator);
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        rackVo.setUpdateBy(operator);
        List<String> collect = vo.getRackList().stream()
                .filter(aLong -> Objects.equals(aLong, rackId))
                .collect(Collectors.toList());
        target.addRack(rackId);
        depotFactory.save(target);
        rackRepository.save(rackVo);
        if (collect.size() > 0) {
            vo.removeRack(rackId);
            rackVo.setDepotId(newId);
            depotFactory.save(vo);
        }
        List<MaterialInfo> all = MongoDBService.findAll(MaterialInfo.class);
        for (MaterialInfo info : all) {
            DepotVo depotVo = depotFactory.getById(rackFactory.getById(info.getRackId()).getDepotId());
            info.setDepotId(depotVo.getDepotId());
            info.setLocation(depotVo.getDepotName(), depotVo.getPlace());
            MongoDBService.save(info);
        }
        capacityUpdate(vo);
        capacityUpdate(target);
        return true;
    }

    /**
     * 单值查询
     *
     * @param depotId 仓库Id
     * @return vo
     */
    @Override
    public DepotVo queryOne(String depotId) {
        Assert.notNull(depotId, "Id不能为空");
        return depotFactory.getById(depotId);
    }

    /**
     * 查询所包含货架
     *
     * @param depotId depotId
     * @return list
     */
    @Override
    public List<RackVo> queryRacks(String depotId) {
        Assert.notNull(depotId, "Id不能为空");
        DepotVo vo = depotFactory.getById(depotId);
        List<String> rackList = vo.getRackList();
        if (rackList != null) {
            return rackList.stream()
                    .map(aLong -> rackFactory.getById(aLong))
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 多值分页查询
     *
     * @param size     页大小
     * @param pageSize 页序号
     * @param arg1     条件参数一
     * @param arg2     条件参数二
     * @return list
     */
    @Override
    public PageObject<DepotVo> queryList(Integer size, Integer pageSize, String arg1, String arg2) {
        if (StringUtil.hasText(arg1) && StringUtil.hasText(arg2)) {
            if ("depotName".equals(arg1)) {
                return PageUtil.getPage(MongoDBService.page(DepotVo.class, Criteria.where(arg1).regex(".*?" + arg2 + ".*?"), size, pageSize));
            }
            return PageUtil.getPage(MongoDBService.page(DepotVo.class, Criteria.where(arg1).is(arg2), size, pageSize));
        }
        return PageUtil.getPage(MongoDBService.page(DepotVo.class, size, pageSize));
    }

    /**
     * 容量检测
     * 检测当前容量是否超过最大值
     * @param vo vo
     * @return 当检测到临近最大容量或容量超过时, 返回true
     */
    @Override
    public boolean capacityMaxCheck(DepotVo vo) {
        List<String> rackList = vo.getRackList();
        if (rackList.size() > 0) {
            // 当前容量
            long sum = rackList.stream().map(rackId -> rackFactory.getById(rackId))
                    .mapToLong(RackVo::getCapacity)
                    .sum();
            vo.setCapacity(sum);
            MongoDBService.save(vo);
            return vo.getMaxCapacity() <= sum || sum >= vo.getThreshold();
        }
        return false;
    }

    public void capacityUpdate(DepotVo vo) {
        if (vo != null) {
            List<String> rackList = vo.getRackList();
            if (rackList.size() > 0) {
                // 当前容量
                long sum = rackList.stream().map(rackId -> rackFactory.getById(rackId))
                        .mapToLong(RackVo::getCapacity)
                        .sum();
                vo.setCapacity(sum);
                if (sum > vo.getMaxCapacity()) {
                    vo.setMaxCapacity(sum + 100);
                }
                MongoDBService.save(vo);
            }
        }
    }

    /**
     * 设置仓库容量阈值
     * @param depotId   仓库ID
     * @param threshold 阈值
     * @return boolean
     */
    @Override
    public boolean updateThreshold(String depotId, Long threshold, String operator) {
        DataUtil.checkNumbers(depotId, threshold);
        DepotVo depotVo = depotFactory.getById(depotId);
        Assert.notNull(depotVo, ServiceConstant.DEPOT_NOT_FOUND);
        if (threshold <= 1 || threshold >= depotVo.getMaxCapacity()) {
            throw new ArgumentException("阈值超过可设置范围，范围应在(1," + depotVo.getMaxCapacity() + ")内");
        }
        depotVo.setThreshold(threshold);
        depotVo.setUpdateBy(operator);
        depotRepository.save(depotVo);
        return true;
    }

    /**
     * 查询所有
     *
     * @return list
     */
    @Override
    public List<DepotVo> queryAll() {
        depotFactory.syncData();
        return MongoDBService.findAll(DepotVo.class);
    }
}
