package com.lyne.module.material.listener;

import com.lyne.common.core.types.MaterialRack;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.base.BaseListener;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.material.service.RackService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 货架队列监听
 * @author lyne
 */
@Service
public class RackListener extends BaseListener<MaterialRack> {

    @Resource
    private RackService rackService;

    public RackListener() {
        super("RackListener");
    }

    /**
     * 货架监听
     *
     * @param message 数据
     */
    @Override
    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_RACK)
    public void listener(MqMessage<MaterialRack> message, Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        super.listener(message, channel, tag);
    }

    /**
     * 新增事件处理
     * @param rack depot
     * @return true
     */
    @Override
    public boolean insertEvent(String sender, MaterialRack rack) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            if (rack == null) {
                rack = new MaterialRack();
            }
            return rackService.save(rack);
        }
        return false;
    }

    /**
     * 保存事件处理
     * @param rack depot
     * @return true
     */
    @Override
    public boolean saveEvent(String sender, MaterialRack rack) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(rack);
            return rackService.updateById(rack);
        }
        return false;
    }

    /**
     * 删除事件处理
     * @param rack depot
     * @return true
     */
    @Override
    public boolean removeEvent(String sender, MaterialRack rack) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(rack);
            return rackService.removeById(rack.getRackId());
        }
        return false;
    }
}
