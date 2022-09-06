package com.lyne.gateway.utlis;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Request参数处理
 */
public class RequestAddParaUtils {

    public static ServerHttpRequest addPara(ServerWebExchange exchange, String paraName, String paraValue) {
        URI uri = addPara(exchange.getRequest(), paraName, paraValue);
        return exchange.getRequest().mutate().uri(uri).build();
    }

    public static URI addPara(ServerHttpRequest request, String paraName, String paraValue) {
        return addPara(request.getURI(),paraName,paraValue);
    }

    public static URI addPara(URI uri, String paraName, String paraValue) {
        StringBuilder query = new StringBuilder();
        String originalQuery = uri.getRawQuery();
        if (StringUtils.hasText(originalQuery)) {
            query.append(originalQuery);
            if (originalQuery.charAt(originalQuery.length() - 1) != '&') {
                query.append('&');
            }
        }
        query.append(paraName);
        query.append('=');
        query.append(paraValue);
        return UriComponentsBuilder.fromUri(uri).replaceQuery(query.toString()).build(true).toUri();
    }

}

