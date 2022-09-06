package com.lyne.module.material.listener;

import com.lyne.common.core.types.Material;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.core.utils.data.StringUtil;
import com.lyne.common.mq.base.BaseListener;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.material.service.MaterialService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 物料消息队列监听
 *
 * @author lyne
 */
@Service
public class MaterialListener extends BaseListener<Material> {
    @Resource
    private MaterialService materialService;
    public MaterialListener() {
        super("MaterialListener");
    }
    /**
     * 物料监听
     *
     * @param message 数据
     */
    @Override
    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_MATERIAL)
    public void listener(MqMessage<Material> message, Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        super.listener(message, channel, tag);
    }
    /**
     * 新增事件处理
     * @param material depot
     * @return true
     */
    @Override
    public boolean insertEvent(String sender, Material material) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            if (material == null) {
                material = new Material();
            }
            if (StringUtil.isEmpty(material.getMaterialNameZh())) {
                material.setMaterialNameZh(material.getMaterialName());
            }
            return materialService.save(material);
        }
        return false;
    }
    /**
     * 保存事件处理
     * @param material depot
     * @return true
     */
    @Override
    public boolean saveEvent(String sender, Material material) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(material);
            return materialService.updateById(material);
        }
        return false;
    }
    /**
     * 删除事件处理
     * @param material depot
     * @return true
     */
    @Override
    public boolean removeEvent(String sender, Material material) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            DataUtil.checkNulls(material);
            return materialService.removeById(material.getMaterialId());
        }
        return false;
    }
}
