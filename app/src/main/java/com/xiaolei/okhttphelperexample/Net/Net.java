package com.xiaolei.okhttphelperexample.Net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by xiaolei on 2017/7/9.
 */

public interface Net
{
    @FormUrlEncoded
    @POST("geocoding")
    public Call<DataBean> getIndex(@Field("a") String a);
}