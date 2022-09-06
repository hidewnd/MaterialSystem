package com.lyne.common.core.utils.jwt;

import cn.hutool.core.codec.Base64;
import cn.hutool.jwt.JWTException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * jwt工具
 *
 * @author lyne
 */

public class JwtUtils {

    private static final String BEARER = "bearer";
    private static final Integer AUTH_LENGTH = 7;
    private static final String SIGNING_KEY = "test_key";
    private static final String RSA = "RSA";

    /**
     * 获取token字符串
     * 去除前缀
     *
     * @return jwt
     */
    public static String getJwt(String auth) {
        if ((auth != null) && (auth.length() > AUTH_LENGTH)) {
            String headStr = auth.substring(0, 6).toLowerCase();
            if (headStr.compareTo(BEARER) == 0) {
                auth = auth.substring(7);
                return auth;
            }
        }
        return auth;
    }

    public static Claims decode(String token) {
        String jwt = getJwt(token);
        if (token.length() != jwt.length()) {
            return parseJWT(jwt);
        }
        return null;
    }


    /**
     * 解析jwt
     * 密钥模式
     *
     * @param jsonWebToken token串
     * @return Claims
     */
    public static Claims parseJWT(String jsonWebToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(SIGNING_KEY.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(jsonWebToken).getBody();
        } catch (Exception ex) {
            throw new JWTException("token解析异常");
        }
    }

    public static Claims parseJWT(String key, String jwt) {
        PublicKey publicKey = generatePublicKey(key, RSA);
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(jwt).getBody();
    }


    public static Claims parseJWT(String key, String jwt, Long outTime) {
        PublicKey publicKey = generatePublicKey(key, RSA);
        return Jwts.parser()
                .setSigningKey(publicKey)
                .setAllowedClockSkewSeconds(outTime)
                .parseClaimsJws(jwt).getBody();
    }

    private static PublicKey generatePublicKey(String key, String algorithm) {
        try {
            KeyFactory rsa = KeyFactory.getInstance(algorithm);
            return rsa.generatePublic(new X509EncodedKeySpec(Base64.decode(key)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用");
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException("无效密钥");
        }
    }
}
