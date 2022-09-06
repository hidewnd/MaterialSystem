package com.lyne.common.core.utils.data;

import com.lyne.common.core.exception.ArgumentException;

import java.util.Arrays;

/**
 * 数据工具
 * @author lyne
 */
public class DataUtil {

    /**
     * 检查参数中是否存在空值
     * @param args 参数数组
     */
    public static void checkNulls(Object... args) {
        Arrays.stream(args).forEach(arg -> {
            if (arg == null) {
                throw new ArgumentException("null值异常");
            }
            if (arg instanceof String && ((String) arg).length() == 0) {
                throw new ArgumentException("null值异常");
            }
        });
    }

    /**
     * 检查ID值
     * 数值 >= 0
     * @param args 参数数组
     */
    public static void checkNumbers(Object... args) {
        Arrays.stream(args).forEach(arg -> {
            if (arg == null) {
                throw new ArgumentException("错误的数值");
            }
            if (arg instanceof String) {
                if (StringUtil.isEmpty((String) arg) || !StringUtil.isNumeric((String) arg)) {
                    throw new ArgumentException("错误的数值");
                }
            } else if (arg instanceof Integer) {
                if ((Integer) arg < 0) {
                    throw new ArgumentException("错误的数值");
                }
            } else if (arg instanceof Long) {
                if ((Long) arg < 0) {
                    throw new ArgumentException("错误的数值");
                }
            }
        });
    }
}
