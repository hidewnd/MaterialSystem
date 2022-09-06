package com.lyne.module.manager.material.infrastructure.repository;

import com.lyne.api.material.service.FeignMaterialService;
import com.lyne.common.cache.mongo.utils.MongoDBService;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.manager.material.domain.material.module.vo.DepotVo;
import com.lyne.module.manager.material.domain.material.repository.DepotRepository;
import com.lyne.module.manager.material.infrastructure.factories.DepotFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 持久层
 * 仓库
 *
 * @author lyne
 */
@Component
public class DepotRepositoryImpl implements DepotRepository {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private DepotFactory depotFactory;
    @Resource
    private FeignMaterialService feignMaterialService;

    @Override
    public void save(DepotVo vo) {
        MongoDBService.save(vo);
        MaterialDepot depot = depotFactory.convertDepot(vo);
        MqMessage<MaterialDepot> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_SAVE, depot);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_DEPOT, message);
    }

    @Override
    public void delete(DepotVo vo) {
        MaterialDepot depot = depotFactory.convertDepot(vo);
        MqMessage<MaterialDepot> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_DELETE, depot);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_DEPOT, message);
        MongoDBService.remove(vo);
    }

    @Override
    public void insert(MaterialDepot depot) {
        MqMessage<MaterialDepot> message = new MqMessage<>(MqMessage.SENDER_ONE, MqMessage.OPERATE_INSERT, depot);
        rabbitTemplate.convertAndSend(MqConstant.EXCHANGE_MATERIAL, MqConstant.KEY_DEPOT, message);
    }

    @Override
    public String insertSync(MaterialDepot vo) {
        R<String> res = feignMaterialService.saveDepot(vo, SecurityConstants.INNER);
        if (res != null && res.getCode() == HttpStatus.SUCCESS) {
            return res.getData();
        }
        return null;
    }

}
