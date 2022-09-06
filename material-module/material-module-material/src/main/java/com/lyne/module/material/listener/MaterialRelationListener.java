package com.lyne.module.material.listener;

import com.lyne.common.core.types.Material;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.ParseUtil;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.common.mq.utils.RabbitMqUtils;
import com.lyne.module.material.service.RackService;
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
 * 物料关系绑定事务监听
 * @author lyne
 */
@Service
public class MaterialRelationListener {
    @Resource
    private RackService rackService;

    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_RELATION_MATERIAL)
    public void registrationListener(MqMessage<String> message, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            DataUtil.checkNulls(message);
            boolean res = false;
            Material material;
            switch (message.getOperate()) {
                case MqMessage.OPERATE_INSERT:
                    if (MqMessage.SENDER_LIST.equals(message.getSender())) {
                        List<Material> list = ParseUtil.parseList(message.getData(), Material.class);
                        if (list != null && list.size() > 0) {
                            res = rackService.bindMaterial(list);
                        }
                    }
                    if (MqMessage.SENDER_ONE.equals(message.getSender())) {
                        material = ParseUtil.parseJson(message.getData(), Material.class);
                        Assert.notNull(material, "JSON解析异常");
                        res = rackService.bindMaterial(material.getRackId(), material.getRecordId(),
                                material.getMaterialId(), material.getStock());
                    }
                    break;
                case MqMessage.OPERATE_DELETE:
                    material = ParseUtil.parseJson(message.getData(), Material.class);
                    Assert.notNull(material, "消息数据为空");
                    res = rackService.removeMaterial(material.getRackId(), material.getRecordId(),
                            material.getMaterialId(), material.getStock());
                    break;
                case MqMessage.OPERATE_SAVE:
                    if (MqMessage.SENDER_ONE.equals(message.getSender())) {
                        material = ParseUtil.parseJson(message.getData(), Material.class);
                        Assert.notNull(material, "JSON解析异常");
                        res = rackService.updateMaterial(material.getRackId(), material.getRecordId(),
                                material.getMaterialId(), material.getStock());
                    }
                    if (MqMessage.SENDER_LIST.equals(message.getSender())) {
                        List<Material> list = ParseUtil.parseList(message.getData(), Material.class);
                        if (list != null && list.size() > 0) {
                            res = rackService.updateMaterial(list);
                        }
                    }
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
