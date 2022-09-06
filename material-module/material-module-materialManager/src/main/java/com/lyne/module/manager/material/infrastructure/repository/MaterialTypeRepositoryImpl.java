package com.lyne.module.manager.material.infrastructure.repository;

import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialTypeVo;
import com.lyne.module.manager.material.domain.material.repository.MaterialTypeRepository;
import com.lyne.module.manager.material.infrastructure.factories.MaterialTypeFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 持久层
 * 物料类型
 *
 * @author lyne
 */
@Component
public class MaterialTypeRepositoryImpl implements MaterialTypeRepository {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private MaterialTypeFactory materialTypeFactory;
    @Resource
    private FeignMaterialService feignMaterialService;

    @Override
    public void insert(MaterialType vo) {
        MqMessage<MaterialType> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_INSERT, vo);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_TYPE, message);
    }

    @Override
    public MaterialTypeVo insertSync(MaterialType vo) {
        R<String> res = feignMaterialService.saveMaterialType(vo, SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            return materialTypeFactory.getById(res.getData());
        }
        return null;
    }

    @Async
    @Override
    public void save(MaterialTypeVo vo) {
        DataUtil.checkNulls(vo);
        vo.dataCheck();
        MaterialType type = materialTypeFactory.convertType(vo);
        MqMessage<MaterialType> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_SAVE, type);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_TYPE, message);
        MongoDBService.save(vo);
    }

    @Async
    @Override
    public void delete(MaterialTypeVo vo) {
        DataUtil.checkNulls(vo);
        vo.dataCheck();
        MaterialType type = materialTypeFactory.convertType(vo);
        MqMessage<MaterialType> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_DELETE, type);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_MATERIAL_TYPE, message);
        MongoDBService.remove(vo);
    }


}
