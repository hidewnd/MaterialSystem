package com.lyne.common.core.constant;

/**
 * Http状态码常量
 *
 * @author lyne
 */
public class HttpStatus {

    /*请求成功*/
    public static final int SUCCESS = 200;
    /*对象创建成功*/
    public static final int CREATED = 201;
    /*操作已经执行成功，但是没有返回数据*/
    public static final int NO_CONTENT = 204;


    /*未授权*/
    public static final int UNAUTHORIZED = 401;

    /*访问受限，授权过期*/
    public static final int FORBIDDEN = 403;
    /*资源，服务未找到*/
    public static final int NOT_FOUND = 404;
    /*业务异常*/
    public static final int BUSINESS_ERROR = 405;


    /*系统内部错误*/
    public static final int ERROR = 500;
    /*接口未实现*/
    public static final int NOT_IMPLEMENTED = 501;
    /*服务降级*/
    public static final int SERVICE_DEGRADE = 502;

}
