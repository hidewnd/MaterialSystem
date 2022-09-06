package com.lyne.api.system.service;

import com.lyne.api.system.config.SystemFeignInterceptor;
import com.lyne.api.system.factory.FeignQueryServiceFactory;
import com.lyne.common.core.base.R;
import com.lyne.common.core.constant.SecurityConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 远程查询接口
 *
 * @author lyne
 */
@Component
@FeignClient(contextId = "feignServiceSystem",
        value = "material-module-system",
        fallbackFactory = FeignQueryServiceFactory.class,
        configuration = {SystemFeignInterceptor.class})
public interface FeignSystemService {

    /**
     * 单查询
     */
    @GetMapping("/query/one/{name}/{type}/{values}")
    R<?> queryOne(@PathVariable("name") String name,
                  @PathVariable("type") String type,
                  @PathVariable("values") String values,
                  @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 多查询
     */
    @GetMapping("/list/{name}/{type}")
    R<?> queryList(@PathVariable("name") String name,
                   @PathVariable("type") String type,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/list/{name}/{type}/{column}/{variable1}")
    R<?> queryList(@PathVariable("name") String name,
                   @PathVariable("type") String type,
                   @PathVariable(value = "column", required = false) String column,
                   @PathVariable(value = "variable1", required = false) String variable1,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    @GetMapping("/list/{name}/{type}/{column}/{variable1}/{variable2}")
    R<?> queryList(@PathVariable("name") String name,
                   @PathVariable("type") String type,
                   @PathVariable(value = "column", required = false) String column,
                   @PathVariable(value = "variable1", required = false) String variable1,
                   @PathVariable(value = "variable2", required = false) String variable2,
                   @RequestHeader(SecurityConstants.FROM_SOURCE) String source);


}
