package com.lyne.auth.config.component;

import com.lyne.auth.domain.User;
import com.lyne.common.core.constant.SecurityConstants;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Set;

/**
 * jwt自定义内容
 *
 * @author lyne
 */

public class JwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        HashMap<String, Object> hashMap = new HashMap<>();
        User user = (User) authentication.getUserAuthentication().getPrincipal();
        Set<String> permission = user.getPermission();
        hashMap.put("apis", StringUtils.join(permission.toArray(), ","));
        hashMap.put(SecurityConstants.TOKEN_USER_ID, user.getUserId() + "");
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(hashMap);
        return accessToken;
    }
}
