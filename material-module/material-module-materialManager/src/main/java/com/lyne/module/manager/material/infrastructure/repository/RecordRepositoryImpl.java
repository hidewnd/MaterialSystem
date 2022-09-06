package com.lyne.module.manager.material.infrastructure.repository;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.AtomicDouble;
import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.EnterRecordVo;
import com.lyne.module.manager.material.domain.record.module.aggregate.OuterRecordVo;
import com.lyne.module.manager.material.domain.record.module.vo.RecordVo;
import com.lyne.module.manager.material.domain.record.repository.RecordRepository;
import com.lyne.module.manager.material.infrastructure.factories.MaterialFactory;
import com.lyne.module.manager.material.infrastructure.factories.RecordFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Resource;

/**
 * 持久层
 * 记录实现类
 *
 * @author lyne
 */
@Component
public class RecordRepositoryImpl implements RecordRepository {

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RecordFactory<RecordVo> recordFactory;
    @Resource
    private FeignMaterialService feignMaterialService;
    @Resource
    private MaterialFactory materialFactory;

    /**
     * 数据据更新对象
     * @param vo vo
     */
    @Async
    @Override
    public void save(RecordVo vo) {
        DataUtil.checkNulls(vo);
        Registration convert;
        if (vo.getSign() == Registration.OUTER) {
            // 出库数据校验
            OuterRecordVo outer = OuterRecordVo.getOuter(vo);
            outer.setValue(getTotalValue(vo));
            outer.setNumber(outer.getMaterialNumberMap().values().stream().mapToLong(l -> l).sum());
            MongoDBService.save(outer);
            convert = recordFactory.toRegistration(outer);
        } else {
            convert = recordFactory.toRegistration(vo);
        }
        MqMessage<Registration> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_SAVE, convert);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_REGISTRATION, message);
        saveRelation(vo);
    }

    /**
     * 保存出入库记录中物料信息
     * @param vo vo
     */
    @Async
    @Override
    public void saveRelation(RecordVo vo) {
        DataUtil.checkNulls(vo);
        MongoDBService.save(vo);
        List<RegMaterial> list = new ArrayList<>();
        if (vo.getSign() == Registration.ENTER) {
            // 入库记录转换
            EnterRecordVo record = EnterRecordVo.getEnter(vo);
            Map<String, Long> matNumber = record.getMatNumber();
            matNumber.forEach((key, value) ->
                    list.add(new RegMaterial(record.getRecordId(), record.getSign(), key, value)));
        } else if (vo.getSign() == Registration.OUTER) {
            // 出库记录转换
            OuterRecordVo record = OuterRecordVo.getOuter(vo);
            Map<String, Long> totalNumber = record.getMaterialNumberMap();
            totalNumber.forEach((materialId, number) ->
                    list.add(new RegMaterial(record.getRecordId(), record.getSign(), materialId, number)));
        }
        MqMessage<String> message = new MqMessage<>(MqMessage.SENDER_LIST, MqMessage.OPERATE_SAVE,
                JSONObject.toJSONString(list));
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_RELATION_RECORD, message);
    }

    @Override
    public String insertSync(Registration vo) {
        R<String> res = feignMaterialService.saveRecord(vo, SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            return res.getData();
        }
        return null;
    }

    @Override
    public void insert(Registration registration) {
        MqMessage<Registration> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_INSERT, registration);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_REGISTRATION, message);
    }

    @Override
    public void delete(RecordVo vo) {
        DataUtil.checkNulls(vo);
        Registration convert = recordFactory.toRegistration(vo);
        MqMessage<Registration> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_DELETE, convert);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_REGISTRATION, message);
    }

    @Override
    public Double getTotalValue(RecordVo vo) {
        DataUtil.checkNulls(vo);
        double sum = 0L;
        switch (vo.getSign()) {
            case Registration.ENTER:
                EnterRecordVo enter = EnterRecordVo.getEnter(vo);
                List<String> list = enter.getMaterialList();
                Map<String, Long> numberMap = enter.getMatNumber();
                if (list.size() > 0) {
                    sum = list.stream().map(materialId -> materialFactory.getById(materialId))
                            .filter(Objects::nonNull)
                            .mapToDouble(material -> {
                                Long num = numberMap.get(material.getMaterialId());
                                if (num == null || num <= 0) {
                                    return 0;
                                }
                                return num * material.getValue();
                            }).sum();
                }
                break;
            case Registration.OUTER:
                OuterRecordVo outer = OuterRecordVo.getOuter(vo);
                Map<String, Long> materialMap = outer.getMaterialNumberMap();
                AtomicDouble total = new AtomicDouble(0.0);
                if (materialMap.size() > 0) {
                    materialMap.forEach((materialId, number) -> {
                        MaterialVo materialVo = materialFactory.getById(materialId);
                        if (materialVo != null) {
                            total.addAndGet(materialVo.getValue() * number);
                        }
                    });
                }
                sum = total.get();
                break;
            default:
                return null;
        }
        return sum;
    }

}
