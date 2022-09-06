package com.lyne.gateway.config.compent;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "routers.ignore")
public class IgnoreUrls {
    List<String> urls = new ArrayList<>();

    public IgnoreUrls(List<String> urls) {
        this.urls = urls;
    }

    public IgnoreUrls() {
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }


}