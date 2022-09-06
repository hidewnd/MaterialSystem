package com.lyne.gateway.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTException;
import com.lyne.common.core.utils.servlet.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关统一异常处理
 *
 * @author lyne
 */
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GatewayExceptionHandler.class);

    @Override
    @SuppressWarnings(" NullableProblems")
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        String msg;
        if (ex instanceof NotFoundException) {
            msg = "服务未找到";
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            msg = responseStatusException.getMessage();
        } else {
            msg = ex.getMessage();
        }
        log.error("[网关异常处理] 请求路径:{}, 异常信息:{}", exchange.getRequest().getPath(), ex.getMessage());
        if (ex instanceof JWTException || StrUtil.startWith(msg, "JWT expired at 2022-04-13T00:28:50Z.")) {
            return ServletUtils.webFluxResponseWriter(response, msg, 403);
        }
        ex.printStackTrace();
        return ServletUtils.webFluxResponseWriter(response, msg);
    }
}
