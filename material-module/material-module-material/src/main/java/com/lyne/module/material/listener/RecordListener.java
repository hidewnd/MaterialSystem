package com.lyne.module.material.listener;

import com.lyne.common.core.types.Registration;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.base.BaseListener;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.material.service.RegistrationService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 出入库记录队列监听
 * @author lyne
 */
@Service
public class RecordListener extends BaseListener<Registration> {
    @Resource
    private RegistrationService registrationService;

    public RecordListener() {
        super("RecordListener");
    }

    /**
     * 记录监听
     *
     * @param message 数据
     */
    @Override
    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_REGISTRATION)
    public void listener(MqMessage<Registration> message, Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        super.listener(message, channel, tag);
    }

    @Override
    public boolean insertEvent(String sender, Registration data) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            if (data == null) {
                data = new Registration();
            }
            return registrationService.save(data);
        }
        return false;
    }

    @Override
    public boolean saveEvent(String sender, Registration data) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(data);
            return registrationService.updateById(data);
        }
        return false;
    }

    @Override
    public boolean removeEvent(String sender, Registration data) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(data);
            return registrationService.removeById(data.getRegId());
        }
        return false;
    }
}
