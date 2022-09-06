package com.lyne.module.manager.material.infrastructure.repository;

import com.alibaba.fastjson.JSONObject;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.Material;
import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.manager.material.domain.material.module.vo.RackVo;
import com.lyne.module.manager.material.domain.material.repository.RackRepository;
import com.lyne.module.manager.material.infrastructure.factories.MaterialInfoFactory;
import com.lyne.module.manager.material.infrastructure.factories.RackFactory;
import com.lyne.module.manager.material.infrastructure.utils.exception.CapacityOutOfBoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;

/**
 * 持久层
 * 货架信息
 *
 * @author lyne
 */
@Component
public class RackRepositoryImpl implements RackRepository {
    private static final Logger log = LoggerFactory.getLogger(RackRepository.class);
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RackFactory rackFactory;
    @Resource
    private FeignMaterialService feignMaterialService;
    @Resource
    private MaterialInfoFactory materialInfoFactory;

    /**
     * 持久化实体信息
     * @param vo vo
     */
    @Override
    public void save(RackVo vo) {
        DataUtil.checkNulls(vo);
        MongoDBService.save(vo);
        modifiedCapacity(vo);
        MaterialRack rack = rackFactory.convertRack(vo);
        MqMessage<MaterialRack> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_SAVE, rack);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_RACK, message);
        saveRelation(vo);
    }

    /**
     * 持久化实体关系
     * @param vo vo
     */
    @Override
    public void saveRelation(RackVo vo) {
        DataUtil.checkNulls(vo);
        String rackId = vo.getRackId();
        List<String> materialList = vo.getMaterialList();
        if (materialList.size() > 0) {
            // 数据转换
            List<Material> collect = materialList.stream()
                    .map(materialId -> materialInfoFactory.get(rackId, materialId))
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .map(info -> materialInfoFactory.convertToMaterial(info))
                    .collect(Collectors.toList());
            MqMessage<String> message = new MqMessage<>(MqMessage.SENDER_LIST, MqMessage.OPERATE_SAVE,
                    JSONObject.toJSONString(collect));
            rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_RELATION_MATERIAL, message);
        }
    }

    /**
     * 持久化删除实体
     * @param vo vo
     */
    @Override
    public void delete(RackVo vo) {
        DataUtil.checkNulls(vo);
        MaterialRack rack = rackFactory.convertRack(vo);
        MqMessage<MaterialRack> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_SAVE, rack);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_RACK, message);
        MongoDBService.remove(vo);
    }

    /**
     * 添加实体信息
     * @param vo vo
     */
    @Override
    public void insert(MaterialRack vo) {
        MqMessage<MaterialRack> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_INSERT, vo);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_RACK, message);
    }

    /**
     * 同步添加实体信息
     * @param rack rack
     */
    @Override
    public String insertSync(MaterialRack rack) {
        R<String> res = feignMaterialService.saveRack(rack, SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            return res.getData();
        }
        return null;
    }

    /**
     * 数据校验, 校验当前容量值
     * 对货架容量进行修正更新
     * @param vo vo
     */
    @Override
    public void modifiedCapacity(RackVo vo) {
        DataUtil.checkNulls(vo);
        Long maxCapacity = vo.getMaxCapacity();
        List<String> materialList = vo.getMaterialList();
        if (materialList.size() > 0) {
            // 实时已使用容量
            long sum = materialList.stream()
                    .map(materialId -> materialInfoFactory.get(vo.getRackId(), materialId))
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .mapToLong(info -> info.getNumber() * info.getCapacityRatio())
                    .sum();
            // 容量数据更新
            if (maxCapacity == null) {
                log.error("[{}] 货架编号[{}]尚未设置最大库存容量，请及时处理!当前容量[{}]", "RackRepository", vo.getRackId(), sum);
            }
            vo.setCapacity(sum);
            rackFactory.save(vo);
            // 库存容量检测
            if (maxCapacity != null && maxCapacity > 0) {
                // 容量预警
                if (sum >= (maxCapacity * 0.95)) {
                    log.warn("[{}] 货架编号[{}]容量即将耗尽，请及时处理!当前容量[{}]", "RackRepository", vo.getRackId(), vo.getCapacity());
                }
                // 检测容量是否超限
                if (sum > maxCapacity) {
                    throw new CapacityOutOfBoundException("容量溢出！");
                }
            }
        }
    }
}
