package com.lyne.auth.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.lyne.auth.domain.LoginMsg;
import com.lyne.auth.service.LoginService;
import com.lyne.auth.service.TokenService;
import com.lyne.common.cache.redis.service.RedisService;
import com.lyne.common.core.constant.CacheConstant;
import com.lyne.common.core.constant.HttpStatus;
import com.lyne.common.core.constant.OssConstant;
import com.lyne.common.core.constant.SecurityConstants;
import com.lyne.common.core.utils.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

/**
 *
 * @author lyne
 */

@Service
public class TokenServiceImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    @Resource
    private RedisService redisService;
    @Resource
    private LoginService loginService;

    @Override
    public Map<String, String> tokenLogin(String username, String password) {
        String refreshKey = CacheConstant.REFRESH_PRE + username;
        String accessKey = CacheConstant.ACCESS_PRE + username;
//        HashMap<String, String> map = new HashMap<>();
//        if (StringUtil.hasText(redisService.getCacheObject(accessKey))) {
//            String accessToken = redisService.getCacheObject(accessKey);
//            String refreshToken = redisService.getCacheObject(refreshKey);
//            redisService.setCacheObject(refreshKey, accessToken, (long) SecurityConstants.ACCESS_TOKEN_VALIDITY,
//                    TimeUnit.SECONDS);
//            redisService.setCacheObject(accessKey, refreshToken, (long) SecurityConstants.ACCESS_TOKEN_VALIDITY,
//                    TimeUnit.SECONDS);
//            map.put("access_token", accessToken);
//            map.put("refresh_token", refreshToken);
//            return map;
//        }
        LoginMsg login = loginService.login(username, password, "password", "client", "123");
        Assert.notNull(login, "系统资源服务异常");
        if (login.getCode() == HttpStatus.SUCCESS) {
            //设置refresh缓存
            redisService.setCacheObject(refreshKey, login.getRefreshToken(),
                    (long) SecurityConstants.ACCESS_TOKEN_VALIDITY, TimeUnit.SECONDS);
            redisService.setCacheObject(accessKey, login.getRefreshToken(),
                    (long) SecurityConstants.ACCESS_TOKEN_VALIDITY, TimeUnit.SECONDS);
            //黑名单处理
            redisService.deleteObject(CacheConstant.BLACK_LIST + username);
            return login.getToken();
        }
        return null;
    }

    @Override
    public Map<String, String> tokenRefresh(String refreshToken) {
        LoginMsg login = loginService.refresh("refresh_token", refreshToken, "client", "123");
        if (login.getCode() == HttpStatus.SUCCESS) {
            String refreshKey = CacheConstant.REFRESH_PRE + login.getUsername();
            String accessKey = CacheConstant.ACCESS_PRE + login.getUsername();
            //设置refresh缓存
            redisService.setCacheObject(refreshKey, login.getRefreshToken(),
                    (long) SecurityConstants.ACCESS_TOKEN_VALIDITY, TimeUnit.SECONDS);
            redisService.setCacheObject(accessKey, login.getAccessToken(),
                    (long) SecurityConstants.ACCESS_TOKEN_VALIDITY, TimeUnit.SECONDS);
            //黑名单处理
            redisService.deleteObject(CacheConstant.BLACK_LIST + login.getUsername());
            return login.getToken();
        }
        return null;
    }

    @Override
    public boolean TokenLogOut(String authorization) {
        Claims claims;
        try {
            claims = JwtUtils.parseJWT(redisService.getCacheObject(SecurityConstants.PUBLIC_KEY),
                    JwtUtils.getJwt(authorization));
        } catch (Exception e) {
            log.error("[授权服务] token解析异常, 异常原因： {}", e.getMessage());
            return false;
        }
        if (claims == null) {
            return false;
        }
        String jti = claims.get(SecurityConstants.TOKEN_JTI, String.class);
        String username = claims.get(SecurityConstants.TOKEN_USERNAME, String.class);
        redisService.setCacheObject(CacheConstant.BLACK_LIST + username, jti);
        redisService.deleteObject(CacheConstant.ACCESS_PRE + username);
        redisService.deleteObject(CacheConstant.REFRESH_PRE + username);
        return true;
    }


    @Override
    public Map<String, Object> getOssSignature() {
        String host = "https://" + OssConstant.BUCKET + "." + OssConstant.END_POINT; // host的格式为 bucketname.endpoint
        String dir = "avatar/" + new DateTime().toString("yyyy-MM-dd") + "/"; // 用户上传文件时指定的前缀。
        Map<String, Object> respMap = null;
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(OssConstant.END_POINT, OssConstant.KEY_ID, OssConstant.KEY_SECRET);
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyCons = new PolicyConditions();
            policyCons.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyCons.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyCons);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            respMap = new LinkedHashMap<>();
            //这些参数名必须要这样写，与官方文档一一对应
            respMap.put("OSSAccessKeyId", OssConstant.KEY_ID);
            respMap.put("policy", encodedPolicy);
            respMap.put("Signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            //让服务端返回200,不然，默认会返回204
            //respMap.put("success_action_status", 200);
        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return respMap;
    }

}
