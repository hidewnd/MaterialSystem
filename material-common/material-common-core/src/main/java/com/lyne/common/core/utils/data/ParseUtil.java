package com.lyne.common.core.utils.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author lyne
 */
@Slf4j
public class ParseUtil {

    public static <T> T parseJson(Object obj, Class<T> clazz) {
        try {
            String s = JSONObject.toJSONString(obj);
            return JSONObject.parseObject(s, clazz);
        } catch (Exception e) {
            log.error("JSON解析异常, 异常原因: {}", e.getMessage());
//            // TODO 调试堆栈追踪
//            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseJson(String obj, Class<T> clazz) {
        try {
            return JSON.parseObject(obj, clazz);
        } catch (Exception e) {
            log.error("JSON解析异常, 异常原因: {}", e.getMessage());
            return null;
        }
    }

    public static <T> List<T> parseList(Object obj, Class<T> clazz) {
        try {
            String s = JSONObject.toJSONString(obj);
            return JSONObject.parseArray(s, clazz);
        } catch (Exception e) {
            log.error("JSON解析异常, 异常原因: {}", e.getMessage());
            return null;
        }
    }

    public static <T> List<T> parseList(String obj, Class<T> clazz) {
        try {
            return JSONObject.parseArray(obj, clazz);
        } catch (Exception e) {
            log.error("JSON解析异常, 异常原因: {}", e.getMessage());
            return null;
        }
    }

}
