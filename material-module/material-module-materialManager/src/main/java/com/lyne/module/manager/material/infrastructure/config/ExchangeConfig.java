package com.lyne.module.manager.material.infrastructure.config;

import com.lyne.common.mq.constant.MqConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * rabbit mq
 * 交换机绑定
 * @author lyne
 */
@Configuration
public class ExchangeConfig {

    // ======================
    //      交换机
    // ======================

    /**
     * 交换机
     * 物料管理
     * @return exchange
     */
    @Bean
    public DirectExchange materialManagerExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_MATERIAL_MANAGER);
    }

    /**
     * 死信交换机
     * 物料管理
     * @return exchange
     */
    @Bean
    public DirectExchange materialManagerDlExchange() {
        return new DirectExchange(MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
    }


    // ======================
    //       队列
    // ======================

    /**
     * 消息队列
     * 物料
     * @return queue
     */
    @Bean
    public Queue materialQueue() {
        Map<String, Object> args = new HashMap<>(3);
        // 声明死信交换器
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
        // 声明死信路由键
        args.put("x-dead-letter-routing-key", MqConstant.KEY_MATERIAL);
        // 声明队列消息过期时间 30分钟
        args.put("x-message-ttl", 30 * 60 * 1000);
        // 队列持久
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_MATERIAL, true, false, false, args);
    }

    /**
     * 死信队列
     * 物料类型
     * @return queue
     */
    @Bean
    public Queue materialDlQueue() {
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_TYPE_DL, true);
    }

    /**
     * 消息队列
     * 物料类型
     * @return queue
     */
    @Bean
    public Queue materialTypeQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
        args.put("x-dead-letter-routing-key", MqConstant.KEY_MATERIAL_TYPE);
        args.put("x-message-ttl", 30 * 60 * 1000);
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_TYPE, true, false, false, args);
    }


    /**
     * 死信队列
     * 物料类型
     * @return queue
     */
    @Bean
    public Queue materialTypeDlQueue() {
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_TYPE_DL, true);
    }

    /**
     * 消息队列
     * 货架
     * @return queue
     */
    @Bean
    public Queue rackQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
        args.put("x-dead-letter-routing-key", MqConstant.KEY_RACK);
        args.put("x-message-ttl", 30 * 60 * 1000);
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_RACK, true, false, false, args);
    }

    /**
     * 死信队列
     * 货架
     * @return queue
     */
    @Bean
    public Queue rackDlQueue() {
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_RACK_DL, true);
    }

    /**
     * 消息队列
     * 仓库
     * @return queue
     */
    @Bean
    public Queue depotQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
        args.put("x-dead-letter-routing-key", MqConstant.KEY_DEPOT);
        args.put("x-message-ttl", 30 * 60 * 1000);
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_DEPOT, true, false, false, args);
    }

    /**
     * 死信队列
     * 货架
     * @return queue
     */
    @Bean
    public Queue depotDlQueue() {
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_DEPOT_DL, true);
    }

    /**
     * 消息队列
     * 出入库记录
     * @return queue
     */
    @Bean
    public Queue registrationQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
        args.put("x-dead-letter-routing-key", MqConstant.KEY_REGISTRATION);
        args.put("x-message-ttl", 30 * 60 * 1000);
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_REGISTRATION, true, false, false, args);
    }

    /**
     * 死信队列
     * 出入库记录
     * @return queue
     */
    @Bean
    public Queue registrationDlQueue() {
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_REGISTRATION_DL, true);
    }

    /**
     * 消息队列
     * 物料关系处理
     * @return queue
     */
    @Bean
    public Queue materialRelationQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
        args.put("x-dead-letter-routing-key", MqConstant.KEY_MATERIAL_RELATION_MATERIAL);
        args.put("x-message-ttl", 30 * 60 * 1000);
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_RELATION_MATERIAL, true, false, false, args);
    }

    /**
     * 死信队列
     * 物料关系处理
     * @return queue
     */
    @Bean
    public Queue materialRelationDlQueue() {
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_RELATION_MATERIAL_DL, true);
    }

    /**
     * 消息队列
     * 出入库记录绑定处理
     * @return queue
     */
    @Bean
    public Queue recordRelationQueue() {
        Map<String, Object> args = new HashMap<>(3);
        args.put("x-dead-letter-exchange", MqConstant.EXCHANGE_DL_MATERIAL_MANAGER);
        args.put("x-dead-letter-routing-key", MqConstant.KEY_MATERIAL_RELATION_RECORD);
        args.put("x-message-ttl", 30 * 60 * 1000);
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_RELATION_RECORD, true, false, false, args);
    }
    /**
     * 死信队列
     * 出入库记录绑定处理
     * @return queue
     */
    @Bean
    public Queue recordRelationDlQueue() {
        return new Queue(MqConstant.QUEUE_MATERIAL_MANAGER_RELATION_RECORD_DL, true);
    }


    // ======================
    //       队列绑定
    // ======================

    /**
     * 消息队列绑定
     * 物料关系处理
     * @return binding
     */
    @Bean
    public Binding materialRelationBinding() {
        return BindingBuilder.bind(materialRelationQueue())
                .to(materialManagerExchange())
                .with(MqConstant.KEY_MATERIAL_RELATION_MATERIAL);
    }

    /**
     * 死信队列绑定
     * 物料关系处理
     * @return binding
     */
    @Bean
    public Binding materialRelationDlBinding() {
        return BindingBuilder.bind(materialRelationDlQueue())
                .to(materialManagerDlExchange())
                .with(MqConstant.KEY_MATERIAL_RELATION_MATERIAL);
    }

    /**
     * 消息队列绑定
     * 出入库记录处理
     * @return binding
     */
    @Bean
    public Binding recordRelationBinding() {
        return BindingBuilder.bind(recordRelationQueue())
                .to(materialManagerExchange())
                .with(MqConstant.KEY_MATERIAL_RELATION_RECORD);
    }

    /**
     * 死信队列绑定
     * 出入库记录关系处理
     * @return binding
     */
    @Bean
    public Binding recordRelationDlBinding() {
        return BindingBuilder.bind(recordRelationDlQueue())
                .to(materialManagerDlExchange())
                .with(MqConstant.KEY_MATERIAL_RELATION_RECORD);
    }

    /**
     * 消息队列绑定
     * 物料
     * @return binding
     */
    @Bean
    public Binding materialBinding() {
        return BindingBuilder.bind(materialQueue())
                .to(materialManagerExchange())
                .with(MqConstant.KEY_MATERIAL);
    }

    /**
     * 死信队列绑定
     * 物料
     * @return binding
     */
    @Bean
    public Binding materialDlBinding() {
        return BindingBuilder.bind(materialDlQueue())
                .to(materialManagerDlExchange())
                .with(MqConstant.KEY_MATERIAL);
    }

    /**
     * 消息队列绑定
     * 物料类型
     * @return binding
     */
    @Bean
    public Binding materialTypeBinding() {
        return BindingBuilder.bind(materialTypeQueue())
                .to(materialManagerExchange())
                .with(MqConstant.KEY_MATERIAL_TYPE);
    }

    /**
     * 死信队列绑定
     * 物料类型
     * @return binding
     */
    @Bean
    public Binding materialTypeDlBinding() {
        return BindingBuilder.bind(materialTypeDlQueue())
                .to(materialManagerDlExchange())
                .with(MqConstant.KEY_MATERIAL_TYPE);
    }

    /**
     * 消息队列绑定
     * 货架
     * @return binding
     */
    @Bean
    public Binding rackBinding() {
        return BindingBuilder.bind(rackQueue())
                .to(materialManagerExchange())
                .with(MqConstant.KEY_RACK);
    }

    /**
     * 死信队列绑定
     * 货架
     * @return binding
     */
    @Bean
    public Binding rackDlBinding() {
        return BindingBuilder.bind(rackDlQueue())
                .to(materialManagerDlExchange())
                .with(MqConstant.KEY_RACK);
    }

    /**
     * 消息队列绑定
     * 仓库
     * @return binding
     */
    @Bean
    public Binding depotBinding() {
        return BindingBuilder.bind(depotQueue())
                .to(materialManagerExchange())
                .with(MqConstant.KEY_DEPOT);
    }

    /**
     * 死信队列绑定
     * 仓库
     * @return binding
     */
    @Bean
    public Binding depotDlBinding() {
        return BindingBuilder.bind(depotDlQueue())
                .to(materialManagerDlExchange())
                .with(MqConstant.KEY_DEPOT);
    }

    /**
     * 消息队列绑定
     * 出入库记录
     * @return binding
     */
    @Bean
    public Binding registrationBinding() {
        return BindingBuilder.bind(registrationQueue())
                .to(materialManagerExchange())
                .with(MqConstant.KEY_REGISTRATION);
    }

    /**
     * 死信队列绑定
     * 出入库记录
     * @return binding
     */
    @Bean
    public Binding registrationDlBinding() {
        return BindingBuilder.bind(registrationDlQueue())
                .to(materialManagerDlExchange())
                .with(MqConstant.KEY_REGISTRATION);
    }


}
