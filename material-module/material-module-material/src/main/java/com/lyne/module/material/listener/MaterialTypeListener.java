package com.lyne.module.material.listener;

import com.lyne.common.core.types.MaterialType;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.base.BaseListener;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.material.service.MaterialTypeService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 物料类型队列监听
 * @author lyne
 */
@Service
public class MaterialTypeListener extends BaseListener<MaterialType> {
    @Resource
    private MaterialTypeService materialTypeService;

    public MaterialTypeListener() {
        super("MaterialTypeListener");
    }

    /**
     * 物料类型监听
     *
     * @param message 数据
     */
    @Override
    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_TYPE)
    public void listener(MqMessage<MaterialType> message, Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        super.listener(message, channel, tag);
    }

    /**
     * 新增事件处理
     * @param type material type
     * @return true
     */
    @Override
    public boolean insertEvent(String sender, MaterialType type) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            if (type == null) {
                type = new MaterialType();
            }
            return materialTypeService.save(type);
        }
        return false;
    }

    /**
     * 保存事件处理
     * @param type material type
     * @return true
     */
    @Override
    public boolean saveEvent(String sender, MaterialType type) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(type);
            return materialTypeService.updateById(type);
        }
        return false;
    }

    /**
     * 删除事件处理
     * @param type material type
     * @return true
     */
    @Override
    public boolean removeEvent(String sender, MaterialType type) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(type);
            return materialTypeService.removeById(type.getTypeId());
        }
        return false;
    }
}
