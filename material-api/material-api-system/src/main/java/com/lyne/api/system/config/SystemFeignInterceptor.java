package com.lyne.api.system.config;

import com.lyne.common.core.constant.SecurityConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lyne
 */
@Component
public class SystemFeignInterceptor implements RequestInterceptor {

    /**
     * 设置header传递，将当前服务的header信息传递到下游服务
     */
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String values = request.getHeader(name);
                    // 跳过 content-length
                    if ("content-length".equals(name)) {
                        continue;
                    }
                    template.header(name, values);
                }
            }
            template.header(SecurityConstants.FROM_SOURCE, SecurityConstants.INNER);
        }
    }
}
