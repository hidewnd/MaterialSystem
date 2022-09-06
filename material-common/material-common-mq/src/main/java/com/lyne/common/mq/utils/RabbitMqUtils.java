package com.lyne.common.mq.utils;

import com.lyne.common.mq.domain.MqMessage;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * rabbitmq连接工具
 *
 * @author lyne
 */
@Component
@RequiredArgsConstructor
public class RabbitMqUtils {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqUtils.class);

    @Value("${spring.rabbitmq.host:}")
    private static String host = "localhost";
    @Value("${spring.rabbitmq.port:}")
    private static Integer port = 15672;
    @Value("${spring.rabbitmq.username:}")
    private static String userName = "server";
    @Value("${spring.rabbitmq.password:}")
    private static String password = "server";


    /**
     * @param exgName routingKey obj
     *                MQ消息发送
     * @author panlupeng
     */
    public static void sendMsg(String exgName, String routingKey, Object obj) {
        //创建连接工厂
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(host);
        cf.setPort(port);
        cf.setUsername(userName);
        cf.setPassword(password);
        sendMsg(cf, exgName, routingKey, obj);
    }

    public static void sendMsg(ConnectionFactory cf, String exgName, String routingKey, Object obj) {
        try {
            Connection con = cf.newConnection();
            Channel channel = con.createChannel();
            byte[] body = toBytes(obj);
            channel.basicPublish(exgName, routingKey, new AMQP.BasicProperties(), body);
            channel.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param obj obj
     * @return bytes[]
     * @description: 数据转化
     * @author panlupeng
     * @date 2021/8/25 10:20
     */
    public static byte[] toBytes(Object obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            oos.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 重新入队
     * @param channel channel
     * @param tag tag
     */
    public static void msgNack(Channel channel, long tag, String order) {
        try {
            channel.basicNack(tag, false, true);
            log.warn("[RabbitMQ {}监听] 重新放入队列成功", order);
        } catch (IOException ioException) {
            log.error("[RabbitMQ {}监听] 重新放入队列失败，失败原因:{}", order, ioException.getMessage());
        }
    }

    /**
     * 拒绝消息
     * @param channel channel
     * @param tag tag
     */
    public static <T> void msgReject(MqMessage<T> message, Channel channel, long tag, String queueName) {
        try {
            channel.basicReject(tag, false);
            log.info("[RabbitMQ {}监听] 拒绝[{}]消息成功, 消息内容: {}", queueName, message.getMessageId(), message);
        } catch (IOException ex) {
            log.info("[RabbitMQ {}监听] 拒绝[{}]消息失败,异常原因：'{}'", queueName, message.getMessageId(),
                    ex.getMessage());
        }
    }

    /**
     * 消息确认
     * @param channel channel
     * @param tag tag
     */
    public static <T> void msgAck(MqMessage<T> message, Channel channel, long tag, String queueName) throws IOException {
        channel.basicAck(tag, true);
        log.info("[RabbitMQ {}监听] 处理[{}]消息成功", queueName, message.getMessageId());
    }

}
