package com.lyne.gateway.config.compent;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.PostConstruct;

/**
 * nacos 动态路由
 *
 * @author lyne
 */
@Slf4j
@Component
public class NacosRouterService implements ApplicationEventPublisherAware {


    @Value("${spring.cloud.nacos.config.group:DEFAULT_GROUP}")
    private String group = "DEFAULT_GROUP";

    @Value("${spring.cloud.nacos.server-addr:localhost}")
    private String serverAddress;

    private String dataId = "material-gateway-router";

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    private ApplicationEventPublisher applicationEventPublisher;

    private static final List<String> ROUTE_LIST = new ArrayList<>();

    @PostConstruct
    public void dynamicRouteByNacosListener() {
        try {
            ConfigService configService = NacosFactory.createConfigService(serverAddress);
            String configInfo = configService.getConfig(dataId, group, 5000);
            //首次初始化
            ThreadUtil.execAsync(() -> parseConfigInfo(configInfo));
            configService.addListener(dataId, group, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    parseConfigInfo(configInfo);
                }
            });
        } catch (NacosException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseConfigInfo(String configInfo) {
        try {
            List<RouteDefinition> gatewayRouteDefinitions = JSONObject.parseArray(configInfo,
                    RouteDefinition.class);
            if (gatewayRouteDefinitions.size() == 0) {
                clearRoute();
            }
            for (RouteDefinition routeDefinition : gatewayRouteDefinitions) {
                addRoute(routeDefinition);
            }
            publish();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private void clearRoute() {
        for (String id : ROUTE_LIST) {
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        }
        ROUTE_LIST.clear();
    }

    private void addRoute(RouteDefinition definition) {
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            ROUTE_LIST.add(definition.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    private void publish() {
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this.routeDefinitionWriter));
    }

}
