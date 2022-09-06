package com.lyne.common.mq.domain;

import cn.hutool.core.util.IdUtil;

import java.io.Serializable;

/**
 * RabbitMq消息体
 *
 * @author lyne
 */
public class MqMessage<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 保存
     */
    public static final String OPERATE_SAVE = "save";
    /**
     * 添加
     */
    public static final String OPERATE_INSERT = "insert";
    /**
     *  删除
     */
    public static final String OPERATE_DELETE = "delete";
    /**
     *  one
     */
    public static final String SENDER_ONE = "one";
    /**
     *  list
     */
    public static final String SENDER_LIST = "list";


    /**
     * 消息ID
     */
    private String messageId;
    /**
     * 消息发送者
     */
    private String sender;
    /**
     * 操作
     */
    private String operate;
    /**
     * 消息信息
     */
    private T data;

    public MqMessage() {
    }

    public MqMessage(T data) {
        this.messageId = IdUtil.objectId();
        this.data = data;
    }

    public MqMessage(String operate, T data) {
        this.messageId = IdUtil.objectId();
        this.operate = operate;
        this.sender = "system";
        this.data = data;
    }

    public MqMessage(String sender, String operate, T data) {
        this.messageId = IdUtil.objectId();
        this.sender = sender;
        this.operate = operate;
        this.data = data;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    @Override
    public String toString() {
        return "MqMessage{" +
                "messageId='" + messageId + '\'' +
                ", sender='" + sender + '\'' +
                ", operate='" + operate + '\'' +
                ", data=" + data +
                '}';
    }
}
