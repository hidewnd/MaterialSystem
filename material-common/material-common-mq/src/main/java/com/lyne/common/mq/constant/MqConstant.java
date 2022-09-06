package com.lyne.common.mq.constant;

/**
 * 消息队列常量
 *
 * @author lyne
 */
public class MqConstant {
    public static final String DL_TAG = ".dl";

    /**
     * 交换机 鉴权服务
     */
    public static final String EXCHANGE_AUTH = "material.auth";
    public static final String EXCHANGE_AUTH_DL = EXCHANGE_AUTH + DL_TAG;

    /**
     * 交换机 物料资源
     */
    public static final String EXCHANGE_MATERIAL = "material.material";
    public static final String EXCHANGE_DL_MATERIAL = EXCHANGE_MATERIAL + DL_TAG;

    /**
     * 交换机 系统资源
     */
    public static final String EXCHANGE_SYSTEM = "material.system";
    public static final String EXCHANGE_SYSTEM_DL = EXCHANGE_SYSTEM + DL_TAG;

    /**
     * 交换机 物料管理
     */
    public static final String EXCHANGE_MATERIAL_MANAGER = "material.manager.material";
    public static final String EXCHANGE_DL_MATERIAL_MANAGER = EXCHANGE_MATERIAL + DL_TAG;

    /**
     * 队列
     */
    public static final String QUEUE_MATERIAL_DEPOT = EXCHANGE_MATERIAL + ".depot";
    public static final String QUEUE_MATERIAL_DEPOT_DL = QUEUE_MATERIAL_DEPOT + DL_TAG;

    public static final String QUEUE_MATERIAL_MATERIAL = EXCHANGE_MATERIAL + ".material";
    public static final String QUEUE_MATERIAL_MATERIAL_DL = QUEUE_MATERIAL_MATERIAL + DL_TAG;

    public static final String QUEUE_MATERIAL_RACK = EXCHANGE_MATERIAL + ".rack";
    public static final String QUEUE_MATERIAL_RACK_DL = QUEUE_MATERIAL_RACK + DL_TAG;

    public static final String QUEUE_MATERIAL_REGISTRATION = EXCHANGE_MATERIAL + ".reg";
    public static final String QUEUE_MATERIAL_REGISTRATION_DL = QUEUE_MATERIAL_REGISTRATION + DL_TAG;

    public static final String QUEUE_MATERIAL_TYPE = EXCHANGE_MATERIAL + ".type";
    public static final String QUEUE_MATERIAL_TYPE_DL = QUEUE_MATERIAL_TYPE + DL_TAG;

    public static final String QUEUE_MATERIAL_RELATION_MATERIAL = EXCHANGE_MATERIAL + ".relation.material";
    public static final String QUEUE_MATERIAL_RELATION_MATERIAL_DL = QUEUE_MATERIAL_RELATION_MATERIAL + DL_TAG;
    public static final String QUEUE_MATERIAL_RELATION_RECORD = EXCHANGE_MATERIAL + ".relation.record";
    public static final String QUEUE_MATERIAL_RELATION_RECORD_DL = QUEUE_MATERIAL_RELATION_RECORD + DL_TAG;

    public static final String QUEUE_MATERIAL_MANAGER_DEPOT = EXCHANGE_MATERIAL_MANAGER + ".depot";
    public static final String QUEUE_MATERIAL_MANAGER_DEPOT_DL = QUEUE_MATERIAL_DEPOT + DL_TAG;

    public static final String QUEUE_MATERIAL_MANAGER_MATERIAL = EXCHANGE_MATERIAL_MANAGER + ".material";
    public static final String QUEUE_MATERIAL_MANAGER_MATERIAL_DL = QUEUE_MATERIAL_MATERIAL + DL_TAG;

    public static final String QUEUE_MATERIAL_MANAGER_RACK = EXCHANGE_MATERIAL_MANAGER + ".rack";
    public static final String QUEUE_MATERIAL_MANAGER_RACK_DL = QUEUE_MATERIAL_RACK + DL_TAG;

    public static final String QUEUE_MATERIAL_MANAGER_REGISTRATION = EXCHANGE_MATERIAL_MANAGER + ".reg";
    public static final String QUEUE_MATERIAL_MANAGER_REGISTRATION_DL = QUEUE_MATERIAL_REGISTRATION + DL_TAG;

    public static final String QUEUE_MATERIAL_MANAGER_TYPE = EXCHANGE_MATERIAL_MANAGER + ".type";
    public static final String QUEUE_MATERIAL_MANAGER_TYPE_DL = QUEUE_MATERIAL_TYPE + DL_TAG;

    public static final String QUEUE_MATERIAL_MANAGER_RELATION_MATERIAL = EXCHANGE_MATERIAL_MANAGER + ".relation.material";
    public static final String QUEUE_MATERIAL_MANAGER_RELATION_MATERIAL_DL = QUEUE_MATERIAL_RELATION_MATERIAL + DL_TAG;

    public static final String QUEUE_MATERIAL_MANAGER_RELATION_RECORD = EXCHANGE_MATERIAL_MANAGER + ".relation.record";
    public static final String QUEUE_MATERIAL_MANAGER_RELATION_RECORD_DL = QUEUE_MATERIAL_RELATION_RECORD + DL_TAG;

    /**
     * routing key
     */
    public static final String KEY_DEPOT = "depot";
    public static final String KEY_MATERIAL = "material";
    public static final String KEY_RACK = "rack";
    public static final String KEY_REGISTRATION = "reg";
    public static final String KEY_MATERIAL_TYPE = "mattype";
    public static final String KEY_MATERIAL_RELATION_MATERIAL = "relation.material";
    public static final String KEY_MATERIAL_RELATION_RECORD = "relation.record";

}
