package com.lyne.base;

import android.app.Application;

import com.tencent.mmkv.MMKV;


public class MyApplication extends Application {
    public static MyApplication myContext;

    @Override
    public void onCreate() {
        super.onCreate();
        myContext = this;
        MMKV.initialize(this);
        //初始化全局异常捕捉

    }
}
