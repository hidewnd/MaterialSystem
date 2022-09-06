package com.lyne.module.system.utils;

import com.lyne.common.core.exception.ArgumentException;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * 参数校验
 *
 * @author lyne
 */
public class VerifyUtil {

    /**
     * 检测实体中是否存在属性名
     * @param column 属性名
     * @param clazz 实体类
     */
    public static void columnCheck(String column, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        if (Arrays.stream(fields).noneMatch((e) -> e.getName().equals(column))) {
            throw new ArgumentException("未知参数");
        }
    }

}
