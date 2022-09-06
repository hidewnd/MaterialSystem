package com.lyne.common.security.config;

import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.utils.servlet.ServletUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * feign 请求拦截器
 *
 * @author lyne
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    private transient int mount = 0;

//    @Autowired
//    private FeignOauthService feignOauthService;

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest httpServletRequest = ServletUtils.getRequest();
        if (httpServletRequest != null) {
            Map<String, String> headers = ServletUtils.getHeaders(httpServletRequest);
            // 传递用户信息请求头，防止丢失
            String userId = headers.get(SecurityConstants.TOKEN_USER_ID);
            if (userId != null && !userId.isEmpty()) {
                template.header(SecurityConstants.TOKEN_USER_ID, userId);
            }
            String userName = headers.get(SecurityConstants.DETAILS_USERNAME);
            if (userName != null && !userName.isEmpty()) {
                template.header(SecurityConstants.DETAILS_USERNAME, userName);
            }
            String authentication = headers.get(SecurityConstants.AUTHORIZATION_HEADER);
            if (authentication != null && !authentication.isEmpty()) {
                template.header(SecurityConstants.AUTHORIZATION_HEADER, authentication);
                if (mount > 0) {
                    mount = 0;
                    return;
                }
                mount++;
//                String json = feignOauthService.checkAuth(JwtUtils.getJwt(authentication));
//                if (json != null) {
//                    JSONObject data = JSONUtil.parseObj(JSONUtil.parseObj(json).get("data"));
//                    template.header("apis", (String) data.get("apis"));
//                    if (userName == null || userName.isEmpty()) {
//                        template.header(SecurityConstants.DETAILS_USERNAME, (String) data.get("user_name"));
//                    }
//                }
            }
            // 配置客户端IP
//            template.header("X-Forwarded-For", IpUtils.getIpAddr(ServletUtils.getRequest()));
        }

    }
}
