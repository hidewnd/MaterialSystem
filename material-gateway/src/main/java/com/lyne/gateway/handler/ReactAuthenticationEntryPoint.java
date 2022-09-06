package com.lyne.gateway.handler;

import com.alibaba.fastjson.JSONObject;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.HttpStatus;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 自定义token无效/过期响应
 * @author lyne
 */
@Component
public class ReactAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange serverWebExchange, AuthenticationException e) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        R<?> error = R.fail(HttpStatus.FORBIDDEN, "token无效或过期");
        DataBuffer wrap = response.bufferFactory().wrap(JSONObject.toJSONString(error).getBytes());
        return response.writeWith(Mono.just(wrap));
    }


}
