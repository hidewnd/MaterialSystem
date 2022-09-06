package com.lyne.base.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * @author lyne
 */
public class LocationRsp {

    /**
     * code : 200
     * msg : 获取物料地址信息成功
     * data : {"1484804092155990019":["物料位于test001仓库1484868472671338498号货架， 仓库地址：测试地点1。"],"1484804092155990018":["物料位于test004仓库1484868324360728578号货架， 仓库地址：aa。"],"1498308224414179329":["物料位于test004仓库1484868324360728578号货架， 仓库地址：aa。"],"1514513194528907265":["物料位于test001仓库1484798596384595970号货架， 仓库地址：测试地点1。"]}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private Map<String,List<String>> map;

        public Map<String, List<String>> getMap() {
            return map;
        }

        public void setMap(Map<String, List<String>> map) {
            this.map = map;
        }
    }
}
