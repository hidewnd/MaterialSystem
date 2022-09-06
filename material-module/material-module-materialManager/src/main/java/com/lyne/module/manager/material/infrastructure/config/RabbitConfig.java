package com.lyne.module.manager.material.infrastructure.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lyne
 */
@Configuration
public class RabbitConfig {
    @Value("${spring.rabbitmq.host:}")
    private static String host = null;
    @Value("${spring.rabbitmq.port:}")
    private static Integer port = 5672;
    @Value("${spring.rabbitmq.username:}")
    private static String userName = "server";
    @Value("${spring.rabbitmq.password:}")
    private static String password = "server";

    @Bean("baseConnectionFactory")
    public ConnectionFactory getConnectionFactory() {
        ConnectionFactory cf = new ConnectionFactory();
        cf.setHost(host);
        cf.setPort(port);
        cf.setUsername(userName);
        cf.setPassword(password);
        return cf;
    }
}
