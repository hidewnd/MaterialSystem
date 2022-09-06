package com.lyne.module.manager.material.domain.material.service;

import cn.hutool.core.thread.ThreadUtil;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.PageObject;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.module.manager.material.application.service.DepotService;
import com.lyne.module.manager.material.application.service.RackService;
import com.lyne.module.manager.material.domain.material.module.aggregate.MaterialInfo;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.domain.material.repository.MaterialRepository;
import com.lyne.module.manager.material.domain.material.repository.RackRepository;
import com.lyne.module.manager.material.infrastructure.constant.ServiceConstant;
import com.lyne.module.manager.material.infrastructure.domain.EnterMaterialVo;
import com.lyne.module.manager.material.infrastructure.factories.DepotFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialFactory;
import com.lyne.module.manager.material.infrastructure.factories.MaterialInfoFactory;
import com.lyne.module.manager.material.infrastructure.factories.RackFactory;
import com.lyne.module.manager.material.infrastructure.utils.PageUtil;
import com.lyne.module.manager.material.infrastructure.utils.exception.CapacityOutOfBoundException;
import com.lyne.module.manager.material.infrastructure.utils.exception.MaterialManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * @author lyne
 */
@Service
public class RackServiceImpl implements RackService {
    private static final Logger log = LoggerFactory.getLogger(RackService.class);
    @Resource
    private RackFactory rackFactory;
    @Resource
    private RackRepository rackRepository;
    @Resource
    private DepotFactory depotFactory;
    @Resource
    private MaterialFactory materialFactory;
    @Resource
    private MaterialInfoFactory materialInfoFactory;
    @Resource
    private MaterialRepository materialRepository;
    @Resource
    private DepotService depotService;

    /**
     * 新增货架
     * @param rack 初始数据
     * @return rackId
     */
    @Override
    public String generate(MaterialRack rack) {
        DataUtil.checkNulls(rack);
        String aLong = rackRepository.insertSync(rack);
        ThreadUtil.execAsync(() -> {
            if (rack.getDepotId() != null && aLong != null) {
                DepotVo depotVo = depotFactory.getById(rack.getDepotId());
                depotVo.addList(aLong);
                depotFactory.save(depotVo);
            }
        });
        rackFactory.getById(aLong);
        return aLong;
    }

    /**
     * 修改货架
     * @param rack 货架实体
     * @return boolean
     */
    @Override
    public boolean updateRack(MaterialRack rack, String operator) {
        DataUtil.checkNulls(rack, rack.getRackId());
        RackVo vo = rackFactory.getById(rack.getRackId());
        Assert.notNull(vo, ServiceConstant.RACK_NOT_FOUND);
        String oldDepot = vo.getDepotId();
        vo.setUpdateBy(operator);
        if (rack.getDescription() != null) {
            vo.setDescription(rack.getDescription());
        }
        if (rack.getDepotId() != null) {
            vo.setDepotId(rack.getDepotId());
        }
        if (rack.getMaxCapacity() != null) {
            vo.setMaxCapacity(rack.getMaxCapacity());
        }
        if (rack.getRemark() != null) {
            vo.setRemark(rack.getRemark());
        }
        if (rack.getStatus() != null) {
            vo.setStatus(rack.getStatus());
        }
        rackFactory.save(vo);
        if (rack.getDepotId() != null) {
            depotService.transferRack(oldDepot, rack.getDepotId(), vo.getRackId(), operator);
        }
        return true;
    }

    /**
     * 删除货架
     * @param rackId 货架ID
     * @param operator 操作原
     * @return boolean
     */
    @Override
    public boolean deleteRack(String rackId, String operator) {
        DataUtil.checkNumbers(rackId);
        Assert.notNull(operator, "操作人员不能为空");
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        Long capacity = getPracticalCapacity(rackId);
        if (capacity == null || capacity > 0) {
            throw new MaterialManagerException("请提前清空货架库存");
        }
        int rackVoListSize = MongoDBService.find(MaterialInfo.class, Query.query(Criteria.where("rackId").is(rackId))).size();
        if (rackVoListSize > 0) {
            throw new MaterialManagerException("请提前清空货架库存");
        }
        rackVo.setUpdateBy(operator);
        rackRepository.save(rackVo);
        rackRepository.delete(rackVo);
        return true;
    }

    /**
     * 物料入库
     * 持久化操作
     * 1.绑定物料与货架关系
     * 2.若入库量大于货架可用容量，入库量为可用最大容量，并返回剩余数
     * @param rackId        货架id
     * @param recordId      入库记录id
     * @param matId         物料id
     * @param num           存储数量
     * @return 剩余数 当全部入库成功后返回0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EnterMaterialVo stored(String rackId, String recordId, String matId, Long num, String operator) {
        DataUtil.checkNumbers(matId, rackId, num);
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        rackVo.setCapacity(getPracticalCapacity(rackId));
        MaterialVo materialVo = materialFactory.getById(matId);
        Assert.notNull(rackVo, "该物料不存在");
        // 货架添加物料 返回剩余数
        long remain = rackVo.stored(matId, num * materialVo.getCapacityRatio(), materialVo.getCapacityRatio());
        if ((num - remain) == 0) {
            throw new MaterialManagerException("入库数量不能为0");
        }
        // 物料绑定, 更新materialInfo实体
        String qrcode = materialStored(materialInfoFactory.get(rackId, recordId, matId), rackId, recordId, matId,
                num - remain);
        rackVo.setUpdateBy(operator);
        rackRepository.save(rackVo);
        log.info("[{}] 货架[{}]入库物料[{}]成功, 入库数量：{}", "RackService", rackId, matId, num - remain);
        return new EnterMaterialVo(rackId, recordId, matId, remain, qrcode);
    }

    /**
     * 持久化
     * 物料详情实体数据修改
     * 记录货架-物料关系
     * @param info   实体
     * @param matId  物料id
     * @param rackId 货架id
     * @param num    存储数量
     */
    private String materialStored(MaterialInfo info, String rackId, String recordId, String matId, Long num) {
        if (info == null) {
            // 新<货架,物料>关系时进行添加
            info = materialInfoFactory.insertBind(rackId, recordId, matId, num);
            log.info("[{}] 新增物料详情[{}]", "RackService", info.getId());
        } else {
            // 旧有关系进行数值更新
            info.addMum(num);
            log.info("[{}] 物料详情[{}]更新", "RackService", info.getId());
        }
        materialRepository.saveInfo(info);
        return info.generateQR();
    }

    /**
     * 物料出库
     * 修改库存数据
     * @param matId     物料id
     * @param rackId    货架id
     * @param enterId   入库Id
     * @param num       存储数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void takeOut(String rackId, String enterId, String matId, Long num, String operator) {
        DataUtil.checkNumbers(matId, rackId, num);
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        MaterialInfo info = materialInfoFactory.get(rackId, enterId, matId);
        Assert.notNull(info, "在[" + rackId + "]货架中不存在该物料[" + matId + "]记录");
        Long number = info.getNumber();
        if (number == null || number - num < 0) {
            throw new CapacityOutOfBoundException("该物料库存不足");
        }
        info.cutMum(num);
        rackVo.freeCapacity(num * info.getCapacityRatio());
        rackVo.setUpdateBy(operator);
        // 库存为0时删除货架物料记录
        materialRepository.saveInfo(info);
        if (info.getNumber() == 0) {
            rackVo.removeMaterial(matId);
            materialRepository.deleteInfo(info);
        }
        rackRepository.save(rackVo);
    }

    /**
     * 物料出库
     * 通过出库队列出库
     * 同时修改货架出库队列
     * @param qrcode  物料id
     * @param num    存储数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void takeOut(String qrcode, Long num, String operator) {
        DataUtil.checkNulls(qrcode, num);
        String[] datas = MaterialInfo.decodeQR(qrcode);
        RackVo rack = rackFactory.getById(datas[2]);
        Assert.notNull(rack, ServiceConstant.RACK_NOT_FOUND);
        // 货架出库队列处理
        Long temporaryNumber = rack.getTemporaryQueue().get(qrcode);
        if (temporaryNumber == null || temporaryNumber == 0L) {
            throw new MaterialManagerException("该物料未在出库队列中或已出库完毕");
        }
        rack.removeTemporary(qrcode, num);
        rackFactory.save(rack);
        takeOut(datas[2], datas[0], datas[1], num, operator);
    }

    /**
     * 持久化操作
     * 持久化保存货架对应信息
     * @param rackId rackId
     */
    @Override
    public void saveRack(String rackId) {
        DataUtil.checkNulls(rackId);
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        rackRepository.save(rackVo);
    }

    /**
     * 随机分配物料详情
     * 出库记录自动分配物料
     * <P>从多个货架中获取指定数量的物料</P>
     * <P>当货架的对应库存不足时, 额外选择货架</P>
     * @param materialId 物料ID
     * @param number     待取物料数
     */
    @Override
    public List<MaterialInfo> getMatInfoForOuter(String materialId, Long number) {
        // 获取所有货架数据
        List<RackVo> rackVoList = new ArrayList<>();
        MongoDBService.findAll(DepotVo.class).stream()
                .map(DepotVo::getRackList)
                .forEach(rackVos -> rackVos.forEach(aLong -> {
                    RackVo vo = rackFactory.getById(aLong);
                    if (vo != null) {
                        rackVoList.add(vo);
                    }
                }));
        if (rackVoList.size() <= 0) {
            throw new CapacityOutOfBoundException("暂无库存");
        }
        // 获取同一物料在所有货架中的详情信息
        List<MaterialInfo> list = rackVoList.stream()
                .filter(rack -> rack.getRackId() != null)
                .filter(rack -> rack.getMaterialList() != null && rack.getMaterialList().size() > 0)
                .filter(rack -> rack.getMaterialList().contains(materialId))
                .map(rack -> materialInfoFactory.get(rack.getRackId(), materialId))
                .flatMap(List::stream)
                // 按库存排序
                .sorted(Comparator.comparingLong(MaterialInfo::getNumber).thenComparing(MaterialInfo::getRackId))
                .collect(Collectors.toList());
        // 物料库存总数
        long sum = list.stream().mapToLong(MaterialInfo::getNumber).sum();
        if (list.size() <= 0 || sum < number) {
            throw new CapacityOutOfBoundException("库存不足");
        }
        // 分配的物料详情
        List<MaterialInfo> infoList = new ArrayList<>();
        long n = number;
        for (MaterialInfo materialInfo : list) {
            if (n <= 0) {
                break;
            }
            n -= materialInfo.getNumber();
            infoList.add(materialInfo);
        }
        return infoList;
    }

    //==================
    //      查询操作
    //==================

    /**
     * 获取包含指定物料的货架信息
     * 过滤可用容量为0
     * @param materialId    物料Id
     * @return list
     */
    @Override
    public List<RackVo> getRackForMaterial(String materialId) {
        DataUtil.checkNulls(materialId);
        return MongoDBService.find(MaterialInfo.class, Query.query(Criteria.where("materialId").is(materialId))).stream()
                .map(MaterialInfo::getRackId)
                .map(aLong -> rackFactory.getById(aLong))
                .filter(rack -> rack.getCapacity() > 0)
                .collect(Collectors.toList());
    }


    /**
     * 多值分页查询
     *
     * @param size 页大小
     * @param pageSize 页序号
     * @param arg1 条件参数一
     * @param arg2 条件参数二
     * @return PageObject
     */
    @Override
    public PageObject<RackVo> queryList(Integer size, Integer pageSize, String arg1, String arg2) {
        if (StringUtil.hasText(arg1) && StringUtil.hasText(arg2)) {
            if ("rackId".equals(arg1)) {
                return PageUtil.getPage(MongoDBService.page(RackVo.class,
                        Criteria.where(arg1).regex(arg2 + ".*?"), size, pageSize));
            }
            return PageUtil.getPage(MongoDBService.page(RackVo.class, Criteria.where(arg1).is(arg2), size, pageSize));
        }
        return PageUtil.getPage(MongoDBService.page(RackVo.class, size, pageSize));
    }

    /**
     * 单值擦汗寻
     * @param rackId rackId
     * @return vo
     */
    @Override
    public RackVo queryOne(String rackId) {
        Assert.notNull(rackId, "ID不能为空");
        return rackFactory.getById(rackId);
    }

    /**
     * 计算指定货架的当前实际容量
     * @param rackId 货架Id
     * @return long
     */
    @Override
    public Long getPracticalCapacity(String rackId) {
        DataUtil.checkNulls(rackId);
        RackVo rackVo = rackFactory.getById(rackId);
        Assert.notNull(rackVo, ServiceConstant.RACK_NOT_FOUND);
        if (rackVo.getMaterialList().size() > 0) {
            return rackVo.getMaterialList().stream()
                    .map(aLong -> materialInfoFactory.get(rackId, aLong))
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .mapToLong(info -> info.getNumber() * info.getCapacityRatio())
                    .sum();
        }
        return 0L;
    }


}
