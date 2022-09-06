package com.lyne.common.mq.base;

import com.lyne.common.mq.domain.MqMessage;
import com.lyne.common.mq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.Assert;

/**
 * CRUD监听队列基类
 * @author lyne
 */
public abstract class BaseListener<T> {
    public String queueName;

    public BaseListener(String queueName) {
        this.queueName = queueName;
    }

    /**
     * 新增事件处理
     * @param sender 发送者
     * @param data data
     * @return true
     */
    public abstract boolean insertEvent(String sender, T data);

    /**
     * 保存事件处理
     * @param sender 发送者
     * @param data data
     * @return true
     */
    public abstract boolean saveEvent(String sender, T data);

    /**
     * 删除事件处理
     * @param sender 发送者
     * @param data data
     * @return true
     */
    public abstract boolean removeEvent(String sender, T data);


    public void listener(MqMessage<T> message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        Assert.notNull(message, "null值异常");
        Assert.notNull(channel, "null值异常");
        try {
            if (eventHanding(message)) {
                RabbitMqUtils.msgAck(message, channel, tag, this.getClass().getSimpleName());
            } else {
                channel.basicReject(tag, false);
            }
        } catch (Exception e) {
            RabbitMqUtils.msgReject(message, channel, tag, this.getClass().getSimpleName());
        }
    }

    /**
     * 事件处理
     * @param message 消息
     * @return boolean
     */
    public boolean eventHanding(MqMessage<T> message) {
        boolean res;
        if (message == null) {
            return false;
        }
        switch (message.getOperate()) {
            case MqMessage.OPERATE_INSERT:
                res = insertEvent(message.getSender(), message.getData());
                break;
            case MqMessage.OPERATE_SAVE:
                res = saveEvent(message.getSender(), message.getData());
                break;
            case MqMessage.OPERATE_DELETE:
                res = removeEvent(message.getSender(), message.getData());
                break;
            default:
                return false;
        }
        return res;
    }


}
