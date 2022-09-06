package com.lyne.common.mq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * ReturnCallback 退回模式
 * 如果消息未能投递到目标 queue 里将触发回调 returnCallback
 * 便于方便后续做重发或者补偿
 * @author lyne
 */
@Slf4j
public class RabbitReturnCallbackService implements RabbitTemplate.ReturnsCallback {
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("returnedMessage ===> replyCode={} ,replyText={} ,exchange={} ,routingKey={}",
                returnedMessage.getReplyCode(), returnedMessage.getMessage(), returnedMessage.getExchange(),
                returnedMessage.getRoutingKey());
    }

}
