package com.lyne.module.material.listener;

import com.lyne.common.core.types.MaterialDepot;
import com.lyne.common.core.utils.data.DataUtil;
import com.lyne.common.mq.base.BaseListener;
import com.lyne.common.mq.constant.MqConstant;
import com.lyne.common.mq.domain.MqMessage;
import com.lyne.module.material.service.DepotService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 仓库队列监听
 * @author lyne
 */
@Service
public class DepotListener extends BaseListener<MaterialDepot> {
    @Resource
    private DepotService depotService;

    public DepotListener() {
        super("DepotListener");
    }

    /**
     * 仓库监听
     * @param message 数据
     */
    @Override
    @RabbitHandler
    @RabbitListener(queues = MqConstant.QUEUE_MATERIAL_DEPOT)
    public void listener(MqMessage<MaterialDepot> message, Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        super.listener(message, channel, tag);
    }

    /**
     * 新增事件处理
     * @param depot depot
     * @return true
     * depotService.save(depot)
     */

    @Override
    public boolean insertEvent(String sender, MaterialDepot depot) {
        depot = depot == null ? new MaterialDepot() : depot;
        return depotService.save(depot);
    }

    /**
     * 保存事件处理
     * @param depot depot
     * @return true
     */
    @Override
    public boolean saveEvent(String sender, MaterialDepot depot) {
        DataUtil.checkNulls(depot);
        return depotService.updateById(depot);
    }

    /**
     * 删除事件处理
     * @param depot depot
     * @return true
     */
    @Override
    public boolean removeEvent(String sender, MaterialDepot depot) {
        DataUtil.checkNulls(depot);
        return depotService.removeById(depot.getDepotId());
    }

}

