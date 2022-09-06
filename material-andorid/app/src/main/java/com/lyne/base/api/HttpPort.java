package com.lyne.base.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lyne.base.SpUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * description:
 */
public class HttpPort {
    private static Port INSTANCE;
    //接口地址, 这里只需要修改地址
    public static final String BASE_URL = "http://192.168.3.21:8001";
    public static Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static Port getInstance() {
        INSTANCE = new Retrofit.Builder().baseUrl(BASE_URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(genericClient()).build().create(Port.class);
        return INSTANCE;
    }

    private static OkHttpClient genericClient() {
        //拿保存的token
        String token = SpUtils.get().getValue("access_token", "");
        Log.d("TAG", "Token---" + token);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(chain -> {
            Request original = chain.request();
            String uri = original.url().toString();
            if (uri.equals(BASE_URL + "/auth/token/login")) {
                Request.Builder requestBuilder = original.newBuilder();
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }).addInterceptor(loggingInterceptor).
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).build();
    }
}
