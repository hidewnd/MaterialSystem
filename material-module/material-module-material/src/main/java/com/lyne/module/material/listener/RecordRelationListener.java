package com.lyne.module.material.listener;

import com.lyne.common.core.types.RegMaterial;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.common.mq.utils.RabbitMqUtils;
import com.lyne.module.material.service.RegistrationService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import javax.annotation.Resource;

/**
 * 出入库记录关系处理监听
 * @author lyne
 */
@Service
public class RecordRelationListener {

    @Resource
    private RegistrationService registrationService;

    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_RELATION_RECORD)
    public void registrationListener(MqMessage<String> message, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            DataUtil.checkNulls(message);
            boolean res = false;
            switch (message.getOperate()) {
                case MqMessage.OPERATE_INSERT:
                    if (MqMessage.SENDER_LIST.equals(message.getSender())) {
                        List<RegMaterial> list = ParseUtil.parseList(message.getData(), RegMaterial.class);
                        Assert.notNull(list, "json解析异常");
                        if (list.size() > 0) {
                            res = registrationService.bindMaterial(list);
                        }
                    }
                    if (MqMessage.SENDER_ONE.equals(message.getSender())) {
                        RegMaterial regMaterial = ParseUtil.parseJson(message.getData(), RegMaterial.class);
                        Assert.notNull(regMaterial, "json解析异常");
                        res = registrationService.bindMaterial(regMaterial);
                    }
                    break;
                case MqMessage.OPERATE_SAVE:
                    if (MqMessage.SENDER_ONE.equals(message.getSender())) {
                        RegMaterial regMaterial = ParseUtil.parseJson(message.getData(), RegMaterial.class);
                        Assert.notNull(regMaterial, "json解析异常");
                        res = registrationService.updateMaterial(regMaterial);
                    }
                    if (MqMessage.SENDER_LIST.equals(message.getSender())) {
                        List<RegMaterial> list = ParseUtil.parseList(message.getData(), RegMaterial.class);
                        Assert.notNull(list, "json解析异常");
                        if (list.size() > 0) {
                            res = registrationService.updateMaterial(list);
                        }
                    }
                    break;
                case MqMessage.OPERATE_DELETE:
                    RegMaterial regMaterial = ParseUtil.parseJson(message.getData(), RegMaterial.class);
                    Assert.notNull(regMaterial, "json解析异常");
                    res = registrationService.removeBind(regMaterial.getRegistrationId(), regMaterial.getMaterialId());
                    break;
                default:
                    RabbitMqUtils.msgReject(message, channel, tag, this.getClass().getSimpleName());
            }
            if (res) {
                RabbitMqUtils.msgAck(message, channel, tag, this.getClass().getSimpleName());
            } else {
                RabbitMqUtils.msgReject(message, channel, tag, this.getClass().getSimpleName());
            }
        } catch (Exception e) {
            RabbitMqUtils.msgReject(message, channel, tag, this.getClass().getSimpleName());
        }
    }

}
