package com.lyne.base.api;


import com.lyne.base.bean.LocationRsp;
import com.lyne.base.bean.LoginBody;
import com.lyne.base.bean.LoginRsp;
import com.lyne.base.bean.OuterBody;
import com.lyne.base.bean.OuterSaveRsp;
import com.lyne.base.bean.ProofRsp;
import com.lyne.base.bean.RecordRsp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * description:接口，这里增加接口
 */
public interface Port {
    /**
     * 登录接口
     * LoginRsp ：返回值对应实体类
     * json格式body
     *
     * @Body() LoginBody body：json,
     */
    @POST("/auth/token/login")
    Call<LoginRsp> login(@Body() LoginBody body);

    @POST("/manager/mat/record/outer/verify")
    Call<OuterSaveRsp> outerSave(@Body() OuterBody body);

    /**
     * 获取出库凭证
     * LoginRsp ：返回值对应实体类
     * 表单body
     *
     * @Field("username") 表单键名 ， String username：键值,
     */
    @FormUrlEncoded
    @POST("/manager/mat/record/outer/proof")
    Call<ProofRsp> getProof(@Field("recordId") String recordId);


    /**
     * 获取出库物料地址
     */
    @FormUrlEncoded
    @POST("/manager/mat/record/outer/locations")
    Call<LocationRsp> getLocation(@Field("recordId") String recordId);

    /**
     * 获取出库记录
     */
    @FormUrlEncoded
    @POST("/manager/mat/record/query/page")
    Call<RecordRsp> getRecordList(@Field("type") String type, @Field("page") Integer page,
                                  @Field("size") Integer size, @Field("arg1") String arg1,
                                  @Field("arg2") String arg2);




}
