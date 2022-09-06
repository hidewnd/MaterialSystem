package com.lyne.module.manager.material.infrastructure.listener;

import com.lyne.common.core.types.Material;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.base.BaseListener;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.manager.material.domain.material.module.vo.MaterialVo;
import com.lyne.module.manager.material.infrastructure.factories.MaterialFactory;
import com.lyne.module.manager.material.infrastructure.task.CapacityMonitoringTask;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * 仓库更新监听
 * @author lyne
 */
@Component
public class materialListener extends BaseListener<Material> {

    private static final Logger log = LoggerFactory.getLogger(CapacityMonitoringTask.class);

    @Resource
    private MaterialFactory materialFactory;

    public materialListener() {
        super("materialListener");
    }

    @Override
    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_MANAGER_MATERIAL)
    public void listener(MqMessage<Material> message, Channel channel, long tag) {
        super.listener(message, channel, tag);
    }

    @Override
    public boolean insertEvent(String sender, Material data) {
        return false;
    }

    @Override
    public boolean saveEvent(String sender, Material data) {
        if (MqMessage.SENDER_ONE.equals(sender)) {
            try {
                DataUtil.checkNulls(data);
                MaterialVo materialVo = materialFactory.getById(data.getMaterialId());
                Assert.notNull(materialVo, "null值");
                if (data.getMaterialName() != null) {
                    materialVo.setMaterialName(data.getMaterialName());
                }
                if (data.getMaterialNameZh() != null) {
                    materialVo.setMaterialNameZh(data.getMaterialNameZh());
                }
                materialFactory.save(materialVo);
            } catch (Exception e) {
                log.warn("[RabbitMq materialListener] 处理消息异常: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean removeEvent(String sender, Material data) {
        return false;
    }
}
