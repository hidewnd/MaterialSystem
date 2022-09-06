package com.lyne.common.core.constant;

/**
 * 权限常量
 *
 * @author ruoyi
 */
public class SecurityConstants {
    /**
     * header
     * 令牌自定义标识
     */
    public static final String TOKEN_AUTHENTICATION = "Authorization";

    /**
     * header
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * jwt
     * 用户名字段
     */
    public static final String TOKEN_USERNAME = "user_name";


    /**
     * jwt
     * 用户ID字段
     */
    public static final String TOKEN_USER_ID = "user_id";

    /**
     * jwt
     * jti 唯一ID
     */
    public static final String TOKEN_JTI = "jti";

    public static final String TOKEN_AUTHORITIES = "authorities";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 授权信息字段
     */
    public static final String AUTHORIZATION_HEADER = "authorization";

    /**
     * 请求来源
     */
    public static final String FROM_SOURCE = "from-source";

    /**
     * 内部请求
     */
    public static final String INNER = "inner";

    /**
     * 公钥
     */
    public static final String PUBLIC_KEY = "public_key";
    /**
     * access token 过期时间
     */
    public static final int ACCESS_TOKEN_VALIDITY = 60 * 60 * 24;
    /**
     * refresh token 过期时间
     */
    public static final int REFRESH_TOKEN_VALIDITY = ACCESS_TOKEN_VALIDITY + 60 * 60;
}
