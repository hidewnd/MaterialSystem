package com.lyne.common.core.base;

import com.alibaba.fastjson.JSONObject;
import com.lyne.common.core.constant.HttpStatus;

import java.io.Serializable;

/**
 * 数据载体基类
 *
 * @author lyne
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;

    public R() {
    }

    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    private static <T> R<T> restResult(int code, T data, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public static <T> R<T> ok() {
        return restResult(HttpStatus.SUCCESS, null, null);
    }

    public static <T> R<T> ok(T data) {
        return restResult(HttpStatus.SUCCESS, data, null);
    }

    public static <T> R<T> ok(String msg, T data) {
        return restResult(HttpStatus.SUCCESS, data, msg);
    }

    public static <T> R<T> okMsg(String msg) {
        return restResult(HttpStatus.SUCCESS, null, msg);
    }

    public static <T> R<T> fail() {
        return restResult(HttpStatus.ERROR, null, null);
    }

    public static <T> R<T> fail(String msg) {
        return restResult(HttpStatus.ERROR, null, msg);
    }

    public static <T> R<T> fail(T data) {
        return restResult(HttpStatus.ERROR, data, null);
    }

    public static <T> R<T> fail(T data, String msg) {
        return restResult(HttpStatus.ERROR, data, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return restResult(code, null, msg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + JSONObject.toJSONString(data) +
                '}';
    }
}
