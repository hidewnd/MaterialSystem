package com.lyne.common.core.utils.http;

import com.lyne.common.core.utils.data.StringUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径转权限符
 *
 * @author lyne
 */
public class PathUtils {
    private final static String BACKSLASH = "/";
    private final static String COLON = ":";
    private final static String ASTERISK_FIRST = "*";
    private final static String ASTERISK_SECOND = "**";

    public static String getPerms(String path) {
        return getPerms(path, 3);
    }

    /**
     * 从请求路径中获取权限符
     * @param path 请求路径
     * @param n 最大匹配级
     * @return 权限符
     */
    public static String getPerms(String path, int n) {
        String perms;
        if (!path.startsWith(BACKSLASH)) {
            return null;
        }
        if (path.substring(1).split(BACKSLASH).length < n + 1) {
            perms = path.substring(1).replace(BACKSLASH, COLON);
        } else {
            perms = path.substring(1).replace(BACKSLASH, COLON);
            int index = getIndex(perms, COLON, n);
            perms = perms.substring(0, index);
        }
        return perms;
    }


    public static int getIndex(String str, String chr, int n) {
        Matcher matcher = Pattern.compile(chr).matcher(str);
        int index = 0;
        while (matcher.find()) {
            index++;
            if (index == n) {
                break;
            }
        }
        return matcher.start();
    }

    /**
     * 检测该权限符是否在权限符集中
     * @param str 权限符
     * @param perms 权限符集合
     * @return boolean
     */
    public static boolean inPerms(String str, List<String> perms) {
        if (str != null && perms != null) {
            for (String perm : perms) {
                if (StringUtil.isMatch(perm, str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 权限符转换路径匹配符
     * @param perm 权限符
     * @return 路径匹配符
     */
    public static String permToPath(String perm) {
        String[] split = perm.split(COLON);
        if (split[1].equals(ASTERISK_FIRST)) {
            return BACKSLASH + split[0] + "/**";
        } else if (split[2].equals(ASTERISK_FIRST)) {
            return BACKSLASH + split[0] + BACKSLASH + split[1] + "/**";
        } else {
            return BACKSLASH + perm.replaceAll(COLON, BACKSLASH);
        }
    }

    /**
     * 路径匹配符转换权限符
     * @param path 路径匹配符
     * @return 权限符
     */
    public static String pathToPerm(String path) {
        String[] split = path.substring(1).split(BACKSLASH);
        switch (split.length) {
            case 1:
                if (ASTERISK_SECOND.equals(split[0]) || ASTERISK_FIRST.equals(split[0])) {
                    return "*:*:*";
                }
                return split[0] + ":*:*";
            case 2:
                if (ASTERISK_SECOND.equals(split[0]) || ASTERISK_FIRST.equals(split[0])) {
                    return "*:*:*";
                }
                if (ASTERISK_SECOND.equals(split[1]) || ASTERISK_FIRST.equals(split[1])) {
                    return split[0] + ":*:*";
                }
                return split[0] + COLON + split[1] + ":*";
            default:
                if (split[2].equals(ASTERISK_SECOND)) {
                    split[2] = ASTERISK_FIRST;
                }
                return split[0] + COLON + split[1] + COLON + split[2];
        }
    }
}
